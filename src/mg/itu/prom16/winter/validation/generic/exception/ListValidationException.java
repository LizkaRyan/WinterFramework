package mg.itu.prom16.winter.validation.generic.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mg.itu.prom16.winter.Mapping;
import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.validation.annotation.IfNotValided;

public class ListValidationException extends WinterException {
    List<ValidationException> validations;
    Object object;
    String nameAttribut;
    public ListValidationException(List<ValidationException> listValidation,Object object,String nameAttribut){
        super("Erreur lors de la validation");
        this.validations=listValidation;
        this.object=object;
        this.nameAttribut=nameAttribut;
    }
    @Override
    public String generateWeb(){
        String valiny="";
        valiny="<!DOCTYPE html>\r\n" + //
                        "<html lang=\"en\">\r\n" + //
                        "<head>\r\n" + //
                        "    <meta charset=\"UTF-8\">\r\n" + //
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    <title>Error - "+this.getStatusCode()+"</title>\r\n" + //
                        "</head>\r\n" + //
                        "<style>\r\n" + //
                        "    .box{\r\n" + //
                        "        border: solid #f7493f 1px;\r\n" + //
                        "        padding: 5%;\r\n" + //
                        "        width: 50%;\r\n" + //
                        "        border-radius: 15px;\r\n" + //
                        "    }\r\n" + //
                        "    body{\r\n" + //
                        "        background-color: #0d1017;\r\n" + //
                        "        color: white;\r\n" + //
                        "        display: flex;\r\n" + //
                        "        justify-content: center;\r\n" + //
                        "    }\r\n" + //
                        "</style>\r\n" + //
                        "<body>\r\n" + //
                        "    <div class=\"box\">\r\n" + //
                        "        <div style=\"display: flex;justify-content: center;\">\r\n" + //
                        "            <h3 style=\"color: #f7493f;font-size: xx-large;font-family: sans-serif;\">Erreur - "+this.getStatusCode()+"</h3>\r\n" + //
                        "        </div>\r\n" + //
                        "        <br>\r\n" + //
                        "        <hr style=\"border-color: #f7493f;\">\r\n";
                        for(int i=0;i<this.validations.size();i++){
                            valiny+="<p>"+this.validations.get(i).getMessage()+"</p>\r\n";
                        }
                        valiny+="    </div>\r\n" + //
                        "</body>\r\n" + //
                        "</html>";
        return valiny;
    }

    public List<String> getListMessages(){
        List<String> list=new ArrayList<String>();
        for (int i = 0; i < this.validations.size(); i++) {
            list.add(validations.get(i).getMessage());
        }
        return list;
    }

    public void showError(Mapping mapping,PrintWriter out,HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        if(mapping.getMethod().isAnnotationPresent(IfNotValided.class)){
            IfNotValided ifNotValided=mapping.getMethod().getAnnotation(IfNotValided.class);
            System.out.println("error."+nameAttribut);
            request.setAttribute("error."+nameAttribut,object);
            request.setAttribute("error.messages",this.getListMessages());
            request.getRequestDispatcher(ifNotValided.url()).forward(request, response);
            return;
        }
        out.println(this.generateWeb());
    }
}
