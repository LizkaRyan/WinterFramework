package mg.itu.prom16.exception;

public class UrlNotFoundException extends WinterException {
    public UrlNotFoundException(String url){
        super("Il n'y a pas de methode associe au chemin :\""+url+"\"");
    }
    @Override
    public int getStatusCode() {
        return 404;    
    }
}
