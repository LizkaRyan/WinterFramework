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
        for (Field field : fields) {
            if (field.isAnnotationPresent(this.getAnnotationClass())) {
                field.setAccessible(true);
                Annotation annotation = field.getAnnotation(this.getAnnotationClass());
                ValidationException exception = this.validate(field.get(o), this.getAnnotationClass().cast(annotation));
                if(exception!=null){
                    answer.add(exception);
                }
            }
        }
    }

    public abstract ValidationException validate(Object object,A annotation);
}
