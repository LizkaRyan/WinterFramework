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
    public void validate(Object t, RangeInt annotation) throws ValidationException {
        int value=(int)t;
        if(value<annotation.min()){
            throw new RangeIntException(annotation.champ()+" doit etre superieur a "+annotation.min());
        }
        if(value>annotation.max()){
            throw new RangeIntException(annotation.champ()+" doit etre inferieur a "+annotation.max());
        }
    }
}