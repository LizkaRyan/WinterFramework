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

import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.annotation.Post;
import mg.itu.prom16.annotation.RestController;
import mg.itu.prom16.annotation.Url;
import mg.itu.prom16.enumeration.Verb;
import mg.itu.prom16.exception.DuplicatedUrlException;
import mg.itu.prom16.exception.MethodException;
import mg.itu.prom16.exception.NoControllerFoundException;
import mg.itu.prom16.exception.PackageNotFoundException;
import mg.itu.prom16.exception.PackageXmlNotFoundException;
import mg.itu.prom16.exception.ReturnTypeException;
import mg.itu.prom16.exception.UrlNotFoundException;
import mg.itu.prom16.exception.WinterException;

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
                                if(clazz.isAnnotationPresent(Controller.class)){
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
                Url annotation = methods[e].getAnnotation(Url.class);
                if(methods[e].isAnnotationPresent(Post.class)){
                    testMappingException(methods[e], newMapping, post, annotation);
                    post.put(annotation.url(),newMapping);
                }
                else{
                    testMappingException(methods[e], newMapping, get, annotation);
                    get.put(annotation.url(), newMapping);
                }
            }
        }
        valiny.put(Verb.GET, get);
        valiny.put(Verb.POST, post);
        return valiny;
    }

    public void testMappingException(Method method,Mapping newMapping,HashMap<String,Mapping> valiny,Url annotation)throws DuplicatedUrlException,ReturnTypeException{
        if(!(method.getReturnType()==String.class || method.getReturnType()==ModelAndView.class) && !method.isAnnotationPresent(RestController.class)){
            throw new ReturnTypeException(newMapping);
        }
        Mapping mappingExists=valiny.get(annotation.url());
        if(mappingExists!=null){
            throw new DuplicatedUrlException(annotation.url(), mappingExists,newMapping);
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
                response.sendError(HttpServletResponse.SC_NOT_FOUND,new UrlNotFoundException(url).getMessage());
            }
            else{
                throw new MethodException(methodUsed.toString(),otherMethod.toString(),url);
            }
            return;
        }
        executeMethod(request, response,mapping);
    }

    protected void executeMethod(HttpServletRequest request, HttpServletResponse response,Mapping mapping)
            throws WinterException, IOException {
        PrintWriter out=response.getWriter();
        try {
            if(mapping.getMethod().isAnnotationPresent(RestController.class)){
                restController(request, response,mapping,out);
            }
            else{
                normalController(request,response,mapping,out);
            }
        } catch (WinterException|IOException e) {
            throw e;
        }
        catch(Exception e){
            out.println(e);
        }
        finally{
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            executeMethod(request,response,Verb.GET);
        } catch (WinterException e) {
            response.sendError(e.getStatusCode(),e.getMessage());
        }
        catch(Exception ex){
            throw ex;
        }    
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            executeMethod(request,response,Verb.POST);
        } catch (WinterException e) {
            response.sendError(e.getStatusCode(),e.getMessage());
        }
        catch(Exception ex){
            throw ex;
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
            Object methodReturn=mapping.invokeMethod(getParameters(request),session);
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
            throw e;
        }
    }
    
    protected void normalController(HttpServletRequest request,HttpServletResponse response,Mapping mapping,PrintWriter out){
        response.setContentType("text/html;charset=UTF-8");
        try {
            this.session.setSession(request.getSession());
            Object methodReturn=mapping.invokeMethod(getParameters(request),session);
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