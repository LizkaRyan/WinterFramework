package mg.itu.prom16.winter.validation.generic;

import mg.itu.prom16.winter.exception.WinterException;

import java.util.Objects;

public class ValidationException extends WinterException {

    String field;

    public ValidationException(String message){
        super("Erreur de validation: "+message);
    }

    public String getField(){
        return this.field;
    }

    void setField(String field){
        this.field=field;
    }

    @Override
    public boolean equals(Object object){
        ValidationException validationException=(ValidationException) object;
        return validationException.getMessage().equals(this.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getMessage());
    }
}
