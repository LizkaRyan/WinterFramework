package mg.itu.prom16.winter.validation.validator;

import mg.itu.prom16.winter.validation.annotation.RangeDouble;
import mg.itu.prom16.winter.validation.exception.RangeDoubleException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.ValidationException;

public class RangeDoubleValidator extends CustomValidator<RangeDouble,Double> {

    @Override
    public ValidationException validate(Double t, RangeDouble annotation) {
        if(t<annotation.min()){
            return new RangeDoubleException(annotation.field()+" doit etre superieur a "+annotation.min());
        }
        if(t>annotation.max()){
            return new RangeDoubleException(annotation.field()+" doit etre inferieur a "+annotation.max());
        }
        return null;
    }
}
