package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.validation.generic.CustomValidator;
import mg.itu.prom16.validation.generic.exception.ValidationException;
import mg.itu.prom16.winter.validation.annotation.Required;
import mg.itu.prom16.winter.validation.exception.RequiredException;

public class RequiredValidator extends CustomValidator<Required> {

    public RequiredValidator(){
        super(Required.class);
    }

    @Override
    public void validate(Object o,Required required)throws ValidationException {
        if(o==null){
            throw new RequiredException(required.message());
        }
    }
    
}
