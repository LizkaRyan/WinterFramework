package mg.itu.prom16.validation.generic.exception;

import mg.itu.prom16.exception.WinterException;

public class ValidationException extends WinterException {
    public ValidationException(String message){
        super("Erreur lors de la validation: "+message);
    }
}
