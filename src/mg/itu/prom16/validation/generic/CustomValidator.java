package mg.itu.prom16.validation.generic;

import java.lang.annotation.Annotation;

public interface CustomValidator<A extends Annotation> {
    public <T> void validate(Object o)throws Exception;
}
