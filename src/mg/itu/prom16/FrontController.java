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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.AnnotationController;
import mg.itu.prom16.annotation.GetUrl;

public class FrontController extends HttpServlet{
    String pack;
    HashMap<String,Mapping> hashMap;

    public void init()throws ServletException{
        super.init();
        scan();
    }

    private void scan(){
        this.pack=this.getInitParameter("controllerPackage");
        try {
            List<Class<?>> listes=getClassesInPackage(this.pack);
            this.hashMap=this.initializeHashMap(listes);
        } catch (Exception e) {
        }
    }

    private List<Class<?>> getClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        while(resources.hasMoreElements()){
            URL resource = resources.nextElement();
            if(resource.getProtocol().equals("file")){
                File directory = new File(URLDecoder.decode(resource.getFile(),"UTF-8"));
                if(directory.exists() && directory.isDirectory()){
                    File[] files=directory.listFiles();
                    for(File file : files){
                        if(file.isFile() && file.getName().endsWith(".class")){
                            String className = this.pack + '.' + file.getName().replace(".class","");
                            Class clazz=Class.forName(className);
                            if(clazz.isAnnotationPresent(AnnotationController.class)){
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    public HashMap<String,Mapping> initializeHashMap(List<Class<?>> classes){
        HashMap<String,Mapping> valiny=new HashMap<String,Mapping>();
        for(int i=0;i<classes.size();i++){
            Method[] methods=classes.get(i).getDeclaredMethods();
            for(int e=0;e<methods.length;e++){
                //System.out.println(methods[i].getName()+" "+methods[i].isAnnotationPresent(GetUrl.class));
                if(methods[e].isAnnotationPresent(GetUrl.class)){
                    Mapping mapping = new Mapping(classes.get(i).getSimpleName(),methods[e].getName());
                    GetUrl annotation = methods[e].getAnnotation(GetUrl.class);
                    valiny.put(annotation.url(),mapping);
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
                out.println("<p>Controller: "+mapping.getClasse()+"</p>");
                out.println("<p>Method: "+mapping.getMethodName()+"</p>");
            }
            else{
                out.println("<p>Il n'y a pas de methode associe a ce chemin</p>");
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
}