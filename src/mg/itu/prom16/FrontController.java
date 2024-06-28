package mg.itu.prom16;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.itu.prom16.annotation.AnnotationController;
import mg.itu.prom16.annotation.GetUrl;
import mg.itu.prom16.exception.DuplicatedUrlException;
import mg.itu.prom16.exception.NoControllerFoundException;
import mg.itu.prom16.exception.PackageNotFoundException;
import mg.itu.prom16.exception.PackageXmlNotFoundException;
import mg.itu.prom16.exception.ReturnTypeException;
import mg.itu.prom16.exception.UrlNotFoundException;

public class FrontController extends HttpServlet{
    String pack;
    HashMap<String,Mapping> hashMap;

    public void init()throws ServletException{
        super.init();
        scan();
    }

    private void scan()throws ServletException{
        this.pack=this.getInitParameter("controllerPackage");
        if(this.pack==null){
            throw new PackageXmlNotFoundException();
        }
        List<Class<?>> listes=getClassesInPackage(this.pack);
        this.hashMap=this.initializeHashMap(listes);
    }

    private List<Class<?>> getClassesInPackage(String packageName) throws ServletException {
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
                                if(clazz.isAnnotationPresent(AnnotationController.class)){
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
        catch(ServletException ex){
            throw ex;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return classes;
    }

    public HashMap<String,Mapping> initializeHashMap(List<Class<?>> classes)throws DuplicatedUrlException,ReturnTypeException{
        HashMap<String,Mapping> valiny=new HashMap<String,Mapping>();
        for(int i=0;i<classes.size();i++){
            Method[] methods=classes.get(i).getDeclaredMethods();
            for(int e=0;e<methods.length;e++){
                Mapping newMapping = new Mapping(classes.get(i),methods[e]);
                if(!(methods[e].getReturnType()==String.class || methods[e].getReturnType()==ModelAndView.class)){
                    throw new ReturnTypeException(newMapping);
                }
                if(methods[e].isAnnotationPresent(GetUrl.class)){
                    GetUrl annotation = methods[e].getAnnotation(GetUrl.class);
                    Mapping mappingExists=valiny.get(annotation.url());
                    if(mappingExists!=null){
                        throw new DuplicatedUrlException(annotation.url(), mappingExists,newMapping);
                    }
                    valiny.put(annotation.url(),newMapping);
                }
            }
        }
        return valiny;
    }

    public static String getRequest(String url){
        String[] segments=url.split("/");
        if(segments.length>1){
            return String.join("/", java.util.Arrays.copyOfRange(segments, 2, segments.length));
        }
        return "";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String url = getRequest(request.getRequestURI());
            Mapping mapping = hashMap.get(url);
            if(mapping!=null){
                try {
                    Session session=tunIntoSession(request.getSession());
                    Object methodReturn=mapping.invokeMethod(request,session);
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
            else{
                response.sendError(HttpServletResponse.SC_NOT_FOUND,new UrlNotFoundException(url).getMessage() );
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
    protected static RequestDispatcher makeRequestDispatcher(ModelAndView modelAndView,HttpServletRequest request){
        HashMap<String,Object> objectsToAdd=modelAndView.getObjects();
        for(String key:objectsToAdd.keySet()){
            request.setAttribute(key, objectsToAdd.get(key));
        }
        return request.getRequestDispatcher(modelAndView.getUrl());
    }
    protected static Session turnIntoSession(HttpSession httpSession){
        Enumeration<String> keys=httpSession.getAttributeNames();
        Session valiny=new Session();
        while (keys.hasMoreElements()) {
            String key=keys.nextElement();
            valiny.add(key,httpSession.getAttribute(key));
        }
        return valiny;
    }
}