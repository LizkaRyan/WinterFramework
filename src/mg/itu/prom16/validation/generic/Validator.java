package mg.itu.prom16.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Validator<A extends Annotation> {
    
    public static <T> void validate(CustomValidator<?> customValidator,Object o)throws Exception{
        Field[] fields=o.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].isAnnotationPresent(customValidator.getAnnotation().getClass())){
                customValidator.validate(fields[i].get(o));
            }
        }
    }
}
