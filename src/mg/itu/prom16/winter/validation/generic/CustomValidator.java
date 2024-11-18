package mg.itu.prom16.winter.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public abstract class CustomValidator<A extends Annotation> {
    private final Class<A> annotationClass;

    public CustomValidator(Class<A> annotationClass){
        this.annotationClass=annotationClass;
    }

    public Class<A> getAnnotationClass() {
        return annotationClass;
    }

    public void validate(Object o,List<ValidationException> answer)throws Exception{
        Field[] fields=o.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].isAnnotationPresent(this.getAnnotationClass())){
                fields[i].setAccessible(true);
                Annotation annotation=fields[i].getAnnotation(this.getAnnotationClass());
                ValidationException exception=this.validate(fields[i].get(o),this.getAnnotationClass().cast(annotation));
                if(exception!=null){
                    answer.add(exception);
                }
            }
        }
    }

    public abstract ValidationException validate(Object object,A annotation);
}
