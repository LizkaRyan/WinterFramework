package mg.itu.prom16;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import mg.itu.prom16.annotation.method.Post;
import mg.itu.prom16.annotation.method.Url;
import mg.itu.prom16.annotation.type.Controller;
import mg.itu.prom16.annotation.type.RestController;
import mg.itu.prom16.enumeration.Verb;
import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.exception.initializing.DuplicatedUrlException;
import mg.itu.prom16.winter.exception.initializing.NoControllerFoundException;
import mg.itu.prom16.winter.exception.initializing.PackageNotFoundException;
import mg.itu.prom16.winter.exception.initializing.PackageXmlNotFoundException;
import mg.itu.prom16.winter.exception.initializing.ReturnTypeException;
import mg.itu.prom16.winter.exception.running.MethodException;
import mg.itu.prom16.winter.exception.running.UrlNotFoundException;
import mg.itu.prom16.winter.Mapping;
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.Session;

@MultipartConfig
public class FrontController extends HttpServlet{
    String pack;
    HashMap<Verb,HashMap<String,Mapping>> hashMap;
    Session session=new Session();

    protected static HashMap<Integer,String> methodTypeServlet;

    public void init()throws ServletException{
        super.init();
        scan();
    }

    private void scan()throws WinterException{
        this.pack=this.getInitParameter("controllerPackage");
        if(this.pack==null){
            throw new PackageXmlNotFoundException();
        }
        List<Class<?>> listes=getClassesInPackage(this.pack);
        this.hashMap=this.initializeHashMap(listes);
    }

    private List<Class<?>> getClassesInPackage(String packageName) throws WinterException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        try{
            Enumeration<URL> resources = classLoader.getResources(path);
            if(!resources.hasMoreElements()){
                throw new PackageNotFoundException(packageName);
            }
            while(resources.hasMoreElements()){
                URL resource = resources.nextElement();
                if(resource.getProtocol().equals("file")){
                    File directory = new File(URLDecoder.decode(resource.getFile(),"UTF-8"));
                    if(directory.exists() && directory.isDirectory()){
                        File[] files=directory.listFiles();
                        for(File file : files){
                            if(file.isFile() && file.getName().endsWith(".class")){
                                String className = this.pack + '.' + file.getName().replace(".class","");
                                Class<?> clazz=Class.forName(className);
                                if(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)){
                                    classes.add(clazz);
                                }
                            }
                        }
                        if(classes.size()==0){
                            throw new NoControllerFoundException(this.pack);
                        }
                    }
                }
            }
        }
        catch(WinterException ex){
            throw ex;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return classes;
    }

    public HashMap<Verb,HashMap<String,Mapping>> initializeHashMap(List<Class<?>> classes)throws DuplicatedUrlException,ReturnTypeException{
        HashMap<Verb,HashMap<String,Mapping>> valiny=new HashMap<Verb,HashMap<String,Mapping>>();
        HashMap<String,Mapping> post=new HashMap<String,Mapping>();
        HashMap<String,Mapping> get=new HashMap<String,Mapping>();
        for(int i=0;i<classes.size();i++){
            Method[] methods=classes.get(i).getDeclaredMethods();
            for(int e=0;e<methods.length;e++){
                Mapping newMapping = new Mapping(classes.get(i),methods[e]);
                if(!methods[e].isAnnotationPresent(Url.class)){
                    continue;
                }
                String url=newMapping.getUrl();
                if(methods[e].isAnnotationPresent(Post.class)){
                    testMappingException(newMapping, post, url);
                    post.put(url,newMapping);
                }
                else{
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
        if(!newMapping.isRest()){
            if(!(method.getReturnType()==String.class || method.getReturnType()==ModelAndView.class)){
                throw new ReturnTypeException(newMapping);
            }
        }
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

    protected void executeMethod(HttpServletRequest request,HttpServletResponse response,Verb methodUsed)throws WinterException, IOException{
        String url = getRequest(request.getRequestURI());
        HashMap<String,Mapping> hashmapping = hashMap.get(methodUsed);
        Mapping mapping=hashmapping.get(url);
        if(mapping==null){
            Verb otherMethod=methodUsed.getOther();
            hashmapping=hashMap.get(otherMethod);
            mapping=hashmapping.get(url);
            if(mapping==null){
                throw new UrlNotFoundException(url);
            }
            else{
                throw new MethodException(methodUsed.toString(),otherMethod.toString(),url);
            }
        }
        executeMethod(request, response,mapping);
    }

    protected void executeMethod(HttpServletRequest request, HttpServletResponse response,Mapping mapping)
            throws WinterException, IOException {
        PrintWriter out=response.getWriter();
        try {
            if(mapping.isRest()){
                restController(request, response,mapping,out);
            }
            else{
                normalController(request,response,mapping,out);
            }
        } catch (WinterException|IOException e) {
            throw e;
        }
        catch(Exception e){
            e.printStackTrace();
            out.println(e);
        }
        finally{
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out=response.getWriter();
        try {
            executeMethod(request,response,Verb.GET);
        } catch (WinterException e) {
            out.println(e.generateWeb());
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
        finally{
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out=response.getWriter();
        try {
            executeMethod(request,response,Verb.POST);
        } catch (WinterException e) {
            e.printStackTrace();
            out.println(e.generateWeb());
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
        finally{
            out.close();
        }
    }

    protected static HashMap<String,String> getParameters(HttpServletRequest request){
        HashMap<String,String> valiny=new HashMap<String,String>();
        Enumeration<String> parametres=request.getParameterNames();
        while(parametres.hasMoreElements()){
            String parametre=parametres.nextElement();
            valiny.put(parametre,request.getParameter(parametre));
        }
        return valiny;
    }

    protected static HashMap<String,Part> getParts(HttpServletRequest request)throws Exception{
        HashMap<String,Part> valiny=new HashMap<String,Part>();
        String contentType = request.getContentType();
        if(contentType!=null){
            if(contentType.toLowerCase().startsWith("multipart/")){
                Collection<Part> parts = request.getParts();
                for (Part part : parts) {
                    String partName = part.getName(); // Nom du champ dans le formulaire
                    valiny.put(partName, part);
                }
                return valiny;
            }
        }
        return valiny;
    }

    protected static RequestDispatcher makeRequestDispatcher(ModelAndView modelAndView,HttpServletRequest request){
        HashMap<String,Object> objectsToAdd=modelAndView.getObjects();
        for(String key:objectsToAdd.keySet()){
            request.setAttribute(key, objectsToAdd.get(key));
        }
        return request.getRequestDispatcher(modelAndView.getUrl());
    }
    protected void restController(HttpServletRequest request,HttpServletResponse response,Mapping mapping,PrintWriter out)throws Exception{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            this.session.setSession(request.getSession());
            Object methodReturn=mapping.invokeMethod(getParameters(request),getParts(request),session);
            String json="";
            if(methodReturn instanceof ModelAndView){
                ModelAndView modelAndView=(ModelAndView)methodReturn;
                json=new Gson().toJson(modelAndView.getObjects());
            }
            else{
                Gson gson=new Gson();
                json=gson.toJson(methodReturn);
            }
            out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    protected void normalController(HttpServletRequest request,HttpServletResponse response,Mapping mapping,PrintWriter out){
        response.setContentType("text/html;charset=UTF-8");
        try {
            this.session.setSession(request.getSession());
            Object methodReturn=mapping.invokeMethod(getParameters(request),getParts(request),session);
            if(methodReturn instanceof ModelAndView){
                makeRequestDispatcher((ModelAndView)methodReturn,request).forward(request, response);
            }
            else{
                out.println(methodReturn);
            }
        } catch (Exception e) {
            out.println(e);
            e.printStackTrace();
        }
    }
}