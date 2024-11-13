package mg.itu.prom16.validation.generic.exception;

public class RequiredException extends ValidationException {
    public RequiredException(){
        super("La valeur ne peut pas être null");
    }
}
