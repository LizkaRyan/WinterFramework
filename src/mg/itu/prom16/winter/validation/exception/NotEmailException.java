package mg.itu.prom16.winter.validation.exception;


import mg.itu.prom16.winter.validation.generic.ValidationException;

public class NotEmailException extends ValidationException {
    public NotEmailException(String value) {
        super(value+" is not an Email");
    }
}
