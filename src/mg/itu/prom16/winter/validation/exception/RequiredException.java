package mg.itu.prom16.winter.validation.exception;

import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public class RequiredException extends ValidationException {
    public RequiredException(String message){
        super(message);
    }
}