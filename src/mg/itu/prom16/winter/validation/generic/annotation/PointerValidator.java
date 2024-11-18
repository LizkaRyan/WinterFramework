package mg.itu.prom16.winter.validation.generic.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.winter.validation.generic.CustomValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PointerValidator {
    Class<? extends CustomValidator<?>> value();
}
