package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.winter.validation.annotation.Required;
import mg.itu.prom16.winter.validation.exception.RequiredException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public class RequiredValidator extends CustomValidator<Required> {

    public RequiredValidator(){
        super(Required.class);
    }

    @Override
    public ValidationException validate(Object o,Required required) {
        if(o==null){
            return new RequiredException(required.message());
        }
        return null;
    }
    
}
