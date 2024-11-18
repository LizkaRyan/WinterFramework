package mg.itu.prom16.winter.exception.initializing;

import mg.itu.prom16.winter.exception.WinterException;

public class NoControllerFoundException extends WinterException{
    public NoControllerFoundException(String packages){
        super("Aucune controlleur n'a été vue dans le dossier :\""+packages+"\"");
    }
}
