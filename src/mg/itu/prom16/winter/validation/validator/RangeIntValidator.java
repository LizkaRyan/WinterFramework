package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.winter.validation.annotation.RangeInt;
import mg.itu.prom16.winter.validation.exception.RangeIntException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.ValidationException;

public class RangeIntValidator extends CustomValidator<RangeInt,Integer> {

    @Override
    public ValidationException validate(Integer t, RangeInt annotation) {
        if(t<annotation.min()){
            return new RangeIntException(annotation.field()+" doit etre superieur a "+annotation.min());
        }
        if(t>annotation.max()){
            return new RangeIntException(annotation.field()+" doit etre inferieur a "+annotation.max());
        }
        return null;
    }
}
