package mg.itu.prom16.validation;

import mg.itu.prom16.validation.annotation.Required;
import mg.itu.prom16.validation.generic.CustomValidator;
import mg.itu.prom16.validation.generic.exception.RequiredException;

public class RequiredValidation extends CustomValidator<Required,String> {

    @Override
    public void validate(Object o)throws Exception {
        if(o==null){
            throw new RequiredException();
        }
    }
    
}
