package mg.itu.prom16.winter.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.winter.validation.generic.annotation.PointerValidator;
import mg.itu.prom16.winter.validation.validator.RangeIntValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@PointerValidator(RangeIntValidator.class)
public @interface RangeInt {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    String field();
}
