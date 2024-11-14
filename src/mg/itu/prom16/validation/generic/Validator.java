package mg.itu.prom16.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class Validator {
    
    public static <A extends Annotation> void validate(CustomValidator<A> customValidator,Object o)throws Exception{
        Field[] fields=o.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].isAnnotationPresent(customValidator.getAnnotationClass())){
                fields[i].setAccessible(true);
                Annotation annotation=fields[i].getAnnotation(customValidator.getAnnotationClass());
                customValidator.validate(fields[i].get(o),customValidator.getAnnotationClass().cast(annotation));
            }
        }
    }
}
