package mg.itu.prom16.winter.validation.generic.exception;

import mg.itu.prom16.winter.exception.WinterException;

public class ValidationException extends WinterException {
    public ValidationException(String message){
        super("Erreur de validation: "+message);
    }
}
