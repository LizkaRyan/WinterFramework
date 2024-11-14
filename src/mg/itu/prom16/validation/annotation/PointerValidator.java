package mg.itu.prom16.validation.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.validation.generic.CustomValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PointerValidator {
    Class<? extends CustomValidator<?>> value();
}
