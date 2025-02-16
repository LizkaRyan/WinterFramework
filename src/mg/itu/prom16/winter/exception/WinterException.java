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
        // Lire le template HTML
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Erreur - Framework</title>\n" +
                "    <style>\n" +
                "        :root {\n" +
                "            --bg-color: #1a1a1a;\n" +
                "            --text-color: #f3f4f6;\n" +
                "            --error-color: #ef4444;\n" +
                "            --border-color: #374151;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            font-family: system-ui, -apple-system, sans-serif;\n" +
                "            line-height: 1.5;\n" +
                "            background: var(--bg-color);\n" +
                "            color: var(--text-color);\n" +
                "            margin: 0;\n" +
                "            padding: 2rem;\n" +
                "        }\n" +
                "\n" +
                "        .error-container {\n" +
                "            max-width: 1200px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "\n" +
                "        .error-header {\n" +
                "            border-bottom: 1px solid var(--border-color);\n" +
                "            padding-bottom: 1rem;\n" +
                "            margin-bottom: 2rem;\n" +
                "        }\n" +
                "\n" +
                "        .error-type {\n" +
                "            color: var(--error-color);\n" +
                "            font-size: 2rem;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        .error-code {\n" +
                "            color: #9ca3af;\n" +
                "            font-size: 1.1rem;\n" +
                "            margin: 0.5rem 0;\n" +
                "        }\n" +
                "\n" +
                "        .error-message {\n" +
                "            font-size: 1.25rem;\n" +
                "            margin: 1rem 0;\n" +
                "            padding: 1rem;\n" +
                "            background: rgba(239, 68, 68, 0.1);\n" +
                "            border-radius: 0.5rem;\n" +
                "        }\n" +
                "\n" +
                "        .stack-trace {\n" +
                "            background: rgba(255, 255, 255, 0.05);\n" +
                "            padding: 1.5rem;\n" +
                "            border-radius: 0.5rem;\n" +
                "            overflow-x: auto;\n" +
                "            font-family: ui-monospace, monospace;\n" +
                "            font-size: 0.9rem;\n" +
                "            white-space: pre;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 768px) {\n" +
                "            body {\n" +
                "                padding: 1rem;\n" +
                "            }\n" +
                "\n" +
                "            .error-type {\n" +
                "                font-size: 1.5rem;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error-container\">\n" +
                "        <div class=\"error-header\">\n" +
                "            <h1 class=\"error-type\">${exceptionClass}</h1>\n" +
                "            <div class=\"error-code\">Code d'erreur: ${errorCode}</div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"error-message\">\n" +
                "            "+this.getMessage()+"\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"stack-trace\">\n" +
                "            ${stackTrace}\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // Remplacer les variables
        return template
                .replace("${exceptionClass}", this.getClass().getSimpleName())
                .replace("${errorCode}", String.valueOf(this.getStatusCode()))
                .replace("${errorMessage}", this.getMessage())
                .replace("${stackTrace}", this.getStackTraceException());
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
        // Lire le template HTML
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Erreur - Framework</title>\n" +
                "    <style>\n" +
                "        :root {\n" +
                "            --bg-color: #1a1a1a;\n" +
                "            --text-color: #f3f4f6;\n" +
                "            --error-color: #ef4444;\n" +
                "            --border-color: #374151;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            font-family: system-ui, -apple-system, sans-serif;\n" +
                "            line-height: 1.5;\n" +
                "            background: var(--bg-color);\n" +
                "            color: var(--text-color);\n" +
                "            margin: 0;\n" +
                "            padding: 2rem;\n" +
                "        }\n" +
                "\n" +
                "        .error-container {\n" +
                "            max-width: 1200px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "\n" +
                "        .error-header {\n" +
                "            border-bottom: 1px solid var(--border-color);\n" +
                "            padding-bottom: 1rem;\n" +
                "            margin-bottom: 2rem;\n" +
                "        }\n" +
                "\n" +
                "        .error-type {\n" +
                "            color: var(--error-color);\n" +
                "            font-size: 2rem;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        .error-code {\n" +
                "            color: #9ca3af;\n" +
                "            font-size: 1.1rem;\n" +
                "            margin: 0.5rem 0;\n" +
                "        }\n" +
                "\n" +
                "        .error-message {\n" +
                "            font-size: 1.25rem;\n" +
                "            margin: 1rem 0;\n" +
                "            padding: 1rem;\n" +
                "            background: rgba(239, 68, 68, 0.1);\n" +
                "            border-radius: 0.5rem;\n" +
                "        }\n" +
                "\n" +
                "        .stack-trace {\n" +
                "            background: rgba(255, 255, 255, 0.05);\n" +
                "            padding: 1.5rem;\n" +
                "            border-radius: 0.5rem;\n" +
                "            overflow-x: auto;\n" +
                "            font-family: ui-monospace, monospace;\n" +
                "            font-size: 0.9rem;\n" +
                "            white-space: pre;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 768px) {\n" +
                "            body {\n" +
                "                padding: 1rem;\n" +
                "            }\n" +
                "\n" +
                "            .error-type {\n" +
                "                font-size: 1.5rem;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error-container\">\n" +
                "        <div class=\"error-header\">\n" +
                "            <h1 class=\"error-type\">${exceptionClass}</h1>\n" +
                "            <div class=\"error-code\">Code d'erreur: ${errorCode}</div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"error-message\">\n" +
                "            "+ex.getMessage()+"\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"stack-trace\">\n" +
                "            ${stackTrace}\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // Remplacer les variables
        return template
                .replace("${exceptionClass}", ex.getClass().getSimpleName())
                .replace("${errorCode}", String.valueOf(500))
                .replace("${stackTrace}", getStackTraceException(ex));
    }
}
