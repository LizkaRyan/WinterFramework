package mg.itu.prom16.winter.validation.generic.exception;

import mg.itu.prom16.winter.exception.WinterException;

import java.util.Objects;

public class ValidationException extends WinterException {
    public ValidationException(String message){
        super("Erreur de validation: "+message);
    }

    @Override
    public boolean equals(Object object){
        ValidationException validationException=(ValidationException) object;
        return validationException.getMessage().equals(this.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getMessage()); // Génère un code de hachage basé sur les champs
    }
}
