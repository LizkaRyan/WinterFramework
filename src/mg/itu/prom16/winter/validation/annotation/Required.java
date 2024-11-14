package mg.itu.prom16.winter.validation.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.validation.annotation.PointerValidator;
import mg.itu.prom16.winter.validation.validator.RequiredValidator;

@PointerValidator(RequiredValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
    String message() default "Une contrainte non null a ete levee";
}
