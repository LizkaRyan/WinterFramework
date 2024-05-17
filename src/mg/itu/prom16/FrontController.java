package mg.itu.prom16;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.AnnotationController;

public class FrontController extends HttpServlet{
    boolean scanned=false;
    String pack;
    static List<String> listes;    

    public void init()throws ServletException{
        super.init();
        scan();
    }

    private void scan(){
        if(!scanned){
            this.pack=this.getInitParameter("controllerPackage");
            try {
                listes=getClassesInPackage(this.pack);
                this.scanned=true;
            } catch (Exception e) {
            }
        }
    }

    private List<String> getClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        List<String> classes = new ArrayList<String>();
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
                                classes.add(file.getName().replace(".class",""));
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<p>Listes :</p><ul>\n");
            for(int i=0;i<listes.size();i++){
                out.println("<li>"+listes.get(i)+"</li>");
            }
            out.println("</ul>");
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