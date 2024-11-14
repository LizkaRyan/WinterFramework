package mg.itu.prom16.winter.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RangeInt {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    String champ();
}
