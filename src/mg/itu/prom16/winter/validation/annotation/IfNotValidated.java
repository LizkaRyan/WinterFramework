package mg.itu.prom16.winter.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.enumeration.Verb;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IfNotValidated {
    String url();
    Verb verb() default Verb.GET;    
}
