package mg.itu.prom16.exception;

import javax.servlet.ServletException;

public class NoControllerFoundException extends ServletException {
    public NoControllerFoundException(String packages){
        super("Aucune controlleur n'a été vue dans le dossier :\""+packages+"\"");
    }
}
