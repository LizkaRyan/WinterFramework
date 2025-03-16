package mg.itu.prom16.winter.validation.exception;

import mg.itu.prom16.winter.validation.generic.ValidationException;

public class RangeIntException extends ValidationException{
    public RangeIntException(String message){
        super(message);
    }
}
