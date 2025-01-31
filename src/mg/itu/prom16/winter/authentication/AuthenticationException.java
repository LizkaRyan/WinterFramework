package mg.itu.prom16.winter.authentication;
import mg.itu.prom16.winter.exception.WinterException;
public class AuthenticationException extends WinterException {

    String urlRedirect;

    public AuthenticationException(String redirection){
        super(redirection);
    }
}
