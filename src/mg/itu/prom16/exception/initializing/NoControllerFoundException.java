package mg.itu.prom16.exception.initializing;

import mg.itu.prom16.exception.WinterException;

public class NoControllerFoundException extends WinterException{
    public NoControllerFoundException(String packages){
        super("Aucune controlleur n'a été vue dans le dossier :\""+packages+"\"");
    }
}
