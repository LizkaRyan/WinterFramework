package mg.itu.prom16.winter.validation.annotation;

import mg.itu.prom16.winter.validation.generic.annotation.PointerValidator;
import mg.itu.prom16.winter.validation.validator.RangeIntValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@PointerValidator(RangeIntValidator.class)
public @interface RangeDouble {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    String field();
}
