package mg.itu.prom16.exception;

import mg.itu.prom16.Mapping;

public class ReturnTypeException extends WinterException {
    public ReturnTypeException(Mapping mapping){
        super("la méthode de retour de la fonction \""+mapping.getMethod().getName()+"\" dans la classe \""+mapping.getController().getSimpleName()+"\" doit être un String ou un ModelAndView");
    }
}
