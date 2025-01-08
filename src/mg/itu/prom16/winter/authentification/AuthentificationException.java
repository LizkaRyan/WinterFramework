package mg.itu.prom16.winter.authentification;
import mg.itu.prom16.winter.exception.WinterException;
public class AuthentificationException extends WinterException {

    String urlRedirect;

    public AuthentificationException(String redirection){
        super(redirection);
    }
}
