package mg.itu.prom16.exception;

public class NoControllerFoundException extends Exception {
    public NoControllerFoundException(String packages){
        super("Aucune controlleur n'a été vue dans le dossier :\""+packages+"\"");
    }
}
