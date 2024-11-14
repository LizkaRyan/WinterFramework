package mg.itu.prom16.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import mg.itu.prom16.validation.generic.exception.ValidationException;

public abstract class CustomValidator<A extends Annotation> {
    private final Class<A> annotationClass;

    public CustomValidator(Class<A> annotationClass){
        this.annotationClass=annotationClass;
    }

    public Class<A> getAnnotationClass() {
        return annotationClass;
    }

    public void validate(Object o)throws Exception{
        Field[] fields=o.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].isAnnotationPresent(this.getAnnotationClass())){
                fields[i].setAccessible(true);
                Annotation annotation=fields[i].getAnnotation(this.getAnnotationClass());
                this.validate(fields[i].get(o),this.getAnnotationClass().cast(annotation));
            }
        }
    }

    public abstract void validate(Object t,A annotation)throws ValidationException;
}
