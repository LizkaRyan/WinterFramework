package mg.itu.prom16.winter.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mg.itu.prom16.winter.validation.generic.annotation.PointerValidator;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public final class Validator {
    
    public static Set<CustomValidator<?,?>> getCustomValidators(Object object)throws Exception{
        Field[] fields=object.getClass().getDeclaredFields();
        Set<CustomValidator<?,?>> valiny=new HashSet<CustomValidator<?,?>>();
        for (Field field : fields) {
            getCustomValidators(field, valiny);
        }
        return valiny;
    }

    public static void getCustomValidators(Field field,Set<CustomValidator<?,?>> list)throws Exception{
        Annotation[] annotations=field.getAnnotations();
        for(int i=0;i<annotations.length;i++){
            if(annotations[i].annotationType().isAnnotationPresent(PointerValidator.class)){
                PointerValidator pointeur=annotations[i].annotationType().getAnnotation(PointerValidator.class);
                Class<? extends CustomValidator<?,?>> classe=pointeur.value();
                CustomValidator<? extends Annotation,?> validateur=classe.getConstructor().newInstance();
                list.add(validateur);
            }
        }
    }

    public static List<ValidationException> validate(Object o)throws Exception{
        return validate(getCustomValidators(o),o);
    }

    public static List<ValidationException> validate(Set<CustomValidator<?,?>> validators,Object o)throws Exception{
        List<ValidationException> answer=new ArrayList<ValidationException>();
        for (CustomValidator<?,?> customValidator : validators) {
            customValidator.validate(o,answer);
        }
        return answer;
    }
}
