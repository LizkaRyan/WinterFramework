package mg.itu.prom16.winter.exception.running;

import mg.itu.prom16.winter.exception.WinterException;

public class ParamNotFoundException extends WinterException {
    public ParamNotFoundException(){
        super("Les arguments qui ne sont pas de type Session doivent être annoté par l'annotion @Param");
    }
}
