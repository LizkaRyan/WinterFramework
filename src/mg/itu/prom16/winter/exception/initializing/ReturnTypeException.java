package mg.itu.prom16.winter.exception.initializing;

import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.Mapping;

public class ReturnTypeException extends WinterException {
    public ReturnTypeException(Mapping mapping){
        super("la méthode de retour de la fonction \""+mapping.getMethod().getName()+"\" dans la classe \""+mapping.getController().getSimpleName()+"\" doit être un String ou un ModelAndView");
    }
}
