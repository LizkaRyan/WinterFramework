package mg.itu.prom16.exception;

import jakarta.servlet.ServletException;
import mg.itu.prom16.Mapping;

public class ReturnTypeException extends ServletException {
    public ReturnTypeException(Mapping mapping){
        super("la méthode de retour de la fonction \""+mapping.getMethod().getName()+"\" dans la classe \""+mapping.getController().getSimpleName()+"\" doit être un String ou un ModelAndView");
    }
}
