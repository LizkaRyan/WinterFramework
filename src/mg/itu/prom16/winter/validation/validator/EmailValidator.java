package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.winter.validation.annotation.Email;
import mg.itu.prom16.winter.validation.exception.NotEmailException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public class EmailValidator extends CustomValidator<Email,String> {
    @Override
    public ValidationException validate(String value, Email annotation) {
        System.out.println(value);
        System.out.println(value.contains("@")+" "+value.contains("."));
        if(!(value.contains("@") && value.contains("."))){
            return new NotEmailException(value);
        }
        return null;
    }
}
