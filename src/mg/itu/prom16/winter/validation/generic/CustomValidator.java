package mg.itu.prom16.winter.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public abstract class CustomValidator<A extends Annotation,T> {
    private final Class<A> annotationClass;

    private final Class<T> typeObject;

    public CustomValidator(){
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            this.annotationClass = (Class<A>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            this.typeObject = (Class<T>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
        } else {
            throw new IllegalArgumentException("Main must be subclassed to determine T");
        }
    }

    void validate(Object o,Set<ValidationException> answer)throws Exception{
        Field[] fields=o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(this.annotationClass)) {
                field.setAccessible(true);
                Annotation annotation = field.getAnnotation(this.annotationClass);
                ValidationException exception = this.validate(this.typeObject.cast(field.get(o)), this.annotationClass.cast(annotation));
                if(exception!=null){
                    answer.add(exception);
                }
            }
        }
    }

    void validate(Object o, Set<ValidationException> answer, Parameter parameter){
        Annotation annotation = parameter.getAnnotation(this.annotationClass);
        ValidationException exception = this.validate(this.typeObject.cast(o), this.annotationClass.cast(annotation));
        if(exception!=null){
            answer.add(exception);
        }
    }

    public abstract ValidationException validate(T object,A annotation);
}
