package mg.itu.prom16.winter.exception.running;

import mg.itu.prom16.winter.exception.WinterException;

public class ParamInjectionNotFoundException extends WinterException{
    public ParamInjectionNotFoundException(){
        super("Erreur lors de l'injection du paramètre");
    }
}
