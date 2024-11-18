package mg.itu.prom16.winter.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;

public class WinterException extends ServletException {
    public WinterException(String message){
        super(message);
    }
    public int getStatusCode(){
        return 500;
    }
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
                        "        <hr style=\"border-color: #f7493f;\">\r\n" + //
                        "        <p>"+this.getStackTraceException()+"</p>\r\n" + //
                        "    </div>\r\n" + //
                        "</body>\r\n" + //
                        "</html>";
        return valiny;
    }

    protected String getStackTraceException(){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        return sw.toString();
    }

    public static String getStackTraceException(Exception ex){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public static String generateWeb(Exception ex){
        String valiny="";
        valiny="<!DOCTYPE html>\r\n" + //
                        "<html lang=\"en\">\r\n" + //
                        "<head>\r\n" + //
                        "    <meta charset=\"UTF-8\">\r\n" + //
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    <title>Error - 500</title>\r\n" + //
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
                        "            <h3 style=\"color: #f7493f;font-size: xx-large;font-family: sans-serif;\">Erreur - 500</h3>\r\n" + //
                        "        </div>\r\n" + //
                        "        <br>\r\n" + //
                        "        <hr style=\"border-color: #f7493f;\">\r\n" + //
                        "        <p>"+getStackTraceException(ex)+"</p>\r\n" + //
                        "    </div>\r\n" + //
                        "</body>\r\n" + //
                        "</html>";
        return valiny;
    }
}
