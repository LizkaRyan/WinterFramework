package mg.itu.prom16.exception;

import javax.servlet.ServletException;

public class ParamNotFoundException extends ServletException {
    public ParamNotFoundException(){
        super("Les arguments qui ne sont pas de type Session doivent être annoté par l'annotion @Param");
    }
}
