package mg.itu.prom16.winter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;

import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import com.google.gson.GsonBuilder;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.type.Controller;
import mg.itu.prom16.winter.annotation.type.RestController;
import mg.itu.prom16.winter.download.DownloadableFile;
import mg.itu.prom16.winter.enumeration.Verb;
import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.exception.initializing.DuplicatedUrlException;
import mg.itu.prom16.winter.exception.initializing.NoControllerFoundException;
import mg.itu.prom16.winter.exception.initializing.PackageNotFoundException;
import mg.itu.prom16.winter.exception.initializing.PackageXmlNotFoundException;
import mg.itu.prom16.winter.exception.initializing.ReturnTypeException;
import mg.itu.prom16.winter.exception.running.MethodException;
import mg.itu.prom16.winter.exception.running.UrlNotFoundException;
import mg.itu.prom16.winter.authentication.AuthenticationException;
import mg.itu.prom16.winter.json.LocalDateAdapter;
import mg.itu.prom16.winter.json.LocalDateTimeAdapter;

@MultipartConfig
public class FrontController extends HttpServlet{
    String pack;
    HashMap<Verb,HashMap<String,Mapping>> hashMap;

    private static String prefix;
    private static String suffix;

    private static String staticFileDirectory;

    public void init()throws ServletException{
        super.init();
        scan();
    }

    private void scan()throws WinterException{
        this.pack=this.getInitParameter("controllerPackage");
        if(this.pack==null){
            throw new PackageXmlNotFoundException();
        }
        prefix =this.getInitParameter("views.prefix");
        if(prefix ==null){
            prefix ="";
        }
        suffix=this.getInitParameter("views.suffix");
        if(suffix==null){
            suffix="";
        }
        staticFileDirectory=this.getInitParameter("static.file.directory");
        if(staticFileDirectory==null){
            staticFileDirectory="public";
        }
        List<Class<?>> listes=getClassesInPackage(this.pack);
        this.hashMap=this.initializeHashMap(listes);
    }

    private List<Class<?>> getClassesInPackage(String packageName) throws WinterException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            if (!resources.hasMoreElements()) {
                throw new PackageNotFoundException(packageName);
            }
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                    if (directory.exists() && directory.isDirectory()) {
                        addClassesFromDirectory(directory, packageName, classes);
                    }
                }
            }
            if (classes.isEmpty()) {
                throw new NoControllerFoundException(packageName);
            }
        } catch (WinterException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return classes;
    }
    
    private void addClassesFromDirectory(File directory, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Parcourir récursivement les sous-dossiers
                    addClassesFromDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)) {
                        classes.add(clazz);
                    }
                }
            }
        }
    }

    public HashMap<Verb,HashMap<String,Mapping>> initializeHashMap(List<Class<?>> classes)throws DuplicatedUrlException,ReturnTypeException{
        HashMap<Verb,HashMap<String,Mapping>> valiny=new HashMap<Verb,HashMap<String,Mapping>>();
        HashMap<String,Mapping> post=new HashMap<String,Mapping>();
        HashMap<String,Mapping> get=new HashMap<String,Mapping>();
        for (Class<?> aClass : classes) {
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                Mapping newMapping = new Mapping(aClass, method);
                if (!newMapping.isAController()) {
                    continue;
                }
                String url = newMapping.getUrl();
                if (method.isAnnotationPresent(Post.class)) {
                    testMappingException(newMapping, post, url);
                    post.put(url, newMapping);
                } else if (method.isAnnotationPresent(Get.class)) {
                    testMappingException(newMapping, get, url);
                    get.put(url, newMapping);
                }
            }
        }
        valiny.put(Verb.GET, get);
        valiny.put(Verb.POST, post);
        return valiny;
    }

    public void testMappingException(Mapping newMapping,HashMap<String,Mapping> mapping,String url)throws DuplicatedUrlException,ReturnTypeException{
        Method method=newMapping.getMethod();
        Mapping mappingExists=mapping.get(url);
        if(mappingExists!=null){
            throw new DuplicatedUrlException(url, mappingExists,newMapping);
        }
    }

    public static String getRequest(String url){
        String[] segments=url.split("/");
        if(segments.length>1){
            return String.join("/", java.util.Arrays.copyOfRange(segments, 2, segments.length));
        }
        return "";
    }

    private void serveStaticResource(String path, HttpServletResponse response) throws IOException {
        // Le chemin absolu vers le répertoire des ressources statiques
        String staticDirectory = getServletContext().getRealPath("/"+staticFileDirectory);
        path=path.replace(staticFileDirectory+"/","\\");
        // Résoudre le fichier demandé
        File file = new File(staticDirectory, path);

        if (file.exists()) {
            // Détecter le type MIME du fichier
            String mimeType = getServletContext().getMimeType(file.getName());
            response.setContentType(mimeType);

            // Envoyer le fichier en réponse
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void executeMethod(HttpServletRequest request, HttpServletResponse response,Verb methodUsed)throws IOException{
        String url = getRequest(request.getRequestURI());
        if(url.startsWith(staticFileDirectory+"/")){
            serveStaticResource(url,response);
            return;
        }
        PrintWriter out=response.getWriter();
        Mapping mapping=null;
        try {
            mapping=getMapping(url, methodUsed);
        } catch (WinterException e) {
            out.println(e.generateWeb());
            return;
        }
        executeMethod(request, response,mapping,out);
        out.close();
    }

    protected Mapping getMapping(String url, Verb verb)throws WinterException{
        HashMap<String,Mapping> hashmapping = hashMap.get(verb);
        Mapping mapping=hashmapping.get(url);
        if(mapping==null){
            Verb otherMethod=verb.getOther();
            hashmapping=hashMap.get(otherMethod);
            mapping=hashmapping.get(url);
            if(mapping==null){
                throw new UrlNotFoundException(url);
            }
            else{
                throw new MethodException(verb.toString(),otherMethod.toString(),url);
            }
        }
        return mapping;
    }

    protected void executeMethod(HttpServletRequest request, HttpServletResponse response,Mapping mapping,PrintWriter out)
            throws IOException {
        try {
            if(mapping.isRest()){
                restController(request, response,mapping,out);
            }
            else{
                Object object=normalController(request,response,mapping);
                if (object instanceof ModelAndView modelAndView) {
                    makeRequestDispatcher(modelAndView, request).forward(request,response);
                }
                else if(object instanceof String message){
                    String redirect="redirect:";
                    if(message.startsWith(redirect)){
                        response.sendRedirect(message.substring(redirect.length()));
                        return;
                    }
                    out.println(object);
                } else if (object instanceof DownloadableFile file) {
                    file.setResponse(response);
                    return;
                } else{
                    throw new ReturnTypeException(mapping);
                }
            }
            HttpSession session=request.getSession();
            session.setAttribute("winter.url",mapping.getUrl());
            session.setAttribute("winter.verb",mapping.getVerb());
        } catch (AuthenticationException e){
            String redirect="redirect:";
            if(e.getMessage().startsWith(redirect)){
                response.sendRedirect(e.getMessage().substring(redirect.length()));
                return;
            }
            out.println(e.generateWeb());
        }
        catch (WinterException e) {
            out.println(e.generateWeb());
        }
        catch(Exception e){
            out.println(WinterException.generateWeb(e));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        executeMethod(request,response, Verb.GET);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        executeMethod(request,response, Verb.POST);
    }

    protected static Map<String,Object> getParameters(HttpServletRequest request){
        HashMap<String,String> valiny=new HashMap<String,String>();
        Enumeration<String> parametres=request.getParameterNames();
        while(parametres.hasMoreElements()){
            String parametre=parametres.nextElement();
            valiny.put(parametre,request.getParameter(parametre));
        }
        return convertToNestedMap(valiny);
    }

    public static Map<String, Object> convertToNestedMap(Map<String, String> flatMap) {
        Map<String, Object> nestedMap = new HashMap<>();

        for (Map.Entry<String, String> entry : flatMap.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            insertIntoNestedMap(nestedMap, keys, entry.getValue(), 0);
        }

        return nestedMap;
    }

    public static Map<String, Object> convertToNestedMapPart(Map<String, Part> flatMap) {
        Map<String, Object> nestedMap = new HashMap<>();

        for (Map.Entry<String, Part> entry : flatMap.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            insertIntoNestedMap(nestedMap, keys, entry.getValue(), 0);
        }

        return nestedMap;
    }

    @SuppressWarnings("unchecked")
    private static void insertIntoNestedMap(Map<String, Object> currentMap, String[] keys, Object value, int index) {
        String key = keys[index];

        if (index == keys.length - 1) {
            // Base case: insert the value
            currentMap.put(key, value);
            return;
        }

        // Recursive case: get or create the next map level
        currentMap.putIfAbsent(key, new HashMap<>());
        Map<String, Object> nextMap = (Map<String, Object>) currentMap.get(key);

        insertIntoNestedMap(nextMap, keys, value, index + 1);
    }

    protected static Map<String,Object> getParts(HttpServletRequest request)throws Exception{
        HashMap<String,Part> valiny=new HashMap<String,Part>();
        String contentType = request.getContentType();
        if(contentType!=null){
            if(contentType.toLowerCase().startsWith("multipart/")){
                Collection<Part> parts = request.getParts();
                for (Part part : parts) {
                    String partName = part.getName(); // Nom du champ dans le formulaire
                    valiny.put(partName, part);
                }
            }
        }
        if(valiny.isEmpty()){
            return getParameters(request);
        }
        return convertToNestedMapPart(valiny);
    }

    protected static RequestDispatcher makeRequestDispatcher(ModelAndView modelAndView,HttpServletRequest request){
        HashMap<String,Object> objectsToAdd=modelAndView.getObjects();
        for(String key:objectsToAdd.keySet()){
            request.setAttribute(key, objectsToAdd.get(key));
        }
        return request.getRequestDispatcher(prefix + "/" + modelAndView.getUrl()+suffix);
    }
    protected void restController(HttpServletRequest request,HttpServletResponse response,Mapping mapping,PrintWriter out)throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        String url = getRequest(request.getRequestURI());
        Object methodReturn=mapping.invokeMethod(getParameters(request),getParts(request),new Session(request.getSession()),url);
        String json="";
        if(methodReturn instanceof ModelAndView modelAndView){
            json=new Gson().toJson(modelAndView.getObjects());
        }
        else{
            Gson gson=new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            json=gson.toJson(methodReturn);
        }
        out.println(json);
    }
    
    protected Object normalController(HttpServletRequest request,HttpServletResponse response,Mapping mapping)throws Exception{
        response.setContentType("text/html;charset=UTF-8");
        String url = getRequest(request.getRequestURI());
        Map<String,Object> parts = getParts(request);
        return mapping.invokeMethod(getParameters(request),parts,new Session(request.getSession()),url);
    }
}