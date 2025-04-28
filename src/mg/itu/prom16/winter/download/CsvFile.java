package mg.itu.prom16.winter.download;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CsvFile implements DownloadableFile{
    private String content;

    private final String fileName;

    public CsvFile(String fileName,String content){
        this.fileName = fileName;
        this.content = content;
    }

    public CsvFile(String fileName, List<?> objects, String separations) throws InvocationTargetException, IllegalAccessException {
        this.fileName = fileName;
        this.content = "";
        this.setContent(objects,separations);
    }

    @Override
    public void setResponse(HttpServletResponse response) throws IOException {
        // Configurer la réponse HTTP
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+".csv\"");

        // Écrire le contenu dans le flux de réponse
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
    }

    private void setContent(List<?> objects,String separations) throws InvocationTargetException, IllegalAccessException {
        if(objects.isEmpty()){
            return;
        }
        List<HeaderGetter> headerGetters = getMethods(objects.get(0).getClass());
        if(headerGetters.isEmpty()){
            return;
        }
        for (HeaderGetter headerGetter:headerGetters){
            content += headerGetter.header+separations;
        }
        content = content.substring(0,content.length()-1);
        content += "\n";
        for (Object object:objects){
            for (HeaderGetter headerGetter:headerGetters){
                String value = headerGetter.getter.invoke(object).toString();
                if(value.contains(separations)){
                    value = "\"" + value + "\"";
                }
                content += value + separations;
            }
            content = content.substring(0,content.length()-1);
            content += "\n";
        }
    }

    public List<HeaderGetter> getMethods(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<HeaderGetter> methods = new ArrayList<>();
        for (Field field:fields){
            String fieldName = field.getName();
            String methodName = "get" + fieldName.substring(0,1).toUpperCase();
            methodName += fieldName.substring(1);
            System.out.println(methodName);
            try{
                methods.add(new HeaderGetter(fieldName,clazz.getMethod(methodName)));
            }
            catch (NoSuchMethodException ignored){

            }
        }
        return methods;
    }

    private class HeaderGetter{
        public String header;
        public Method getter;

        public HeaderGetter(String header,Method getter){
            this.getter = getter;
            this.header = header;
        }
    }
}
