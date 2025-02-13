package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.winter.validation.annotation.RangeInt;
import mg.itu.prom16.winter.validation.exception.RangeIntException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public class RangeIntValidator extends CustomValidator<RangeInt> {
    public RangeIntValidator(){
        super(RangeInt.class);
    }

    @Override
    public ValidationException validate(Object t, RangeInt annotation) {
        int value=(int)t;
        if(value<annotation.min()){
            return new RangeIntException(annotation.field()+" doit etre superieur a "+annotation.min());
        }
        if(value>annotation.max()){
            return new RangeIntException(annotation.field()+" doit etre inferieur a "+annotation.max());
        }
        return null;
    }
}
