package mg.itu.prom16.winter.validation.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

import mg.itu.prom16.winter.validation.generic.annotation.PointerValidator;

public final class ValidatorUtil {
    
    public static Set<CustomValidator<?,?>> getCustomValidators(Object object)throws Exception{
        Field[] fields=object.getClass().getDeclaredFields();
        Set<CustomValidator<?,?>> valiny=new HashSet<CustomValidator<?,?>>();
        for (Field field : fields) {
            getCustomValidators(field.getAnnotations(), valiny);
        }
        return valiny;
    }

    public static void getCustomValidators(Annotation[] annotations,Set<CustomValidator<?,?>> list)throws Exception{
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(PointerValidator.class)) {
                PointerValidator pointeur = annotation.annotationType().getAnnotation(PointerValidator.class);
                Class<? extends CustomValidator<?, ?>> classe = pointeur.value();
                CustomValidator<? extends Annotation, ?> validateur = classe.getConstructor().newInstance();
                list.add(validateur);
            }
        }
    }

    public static Set<ValidationException> validate(Object o)throws Exception{
        return validate(getCustomValidators(o),o);
    }

    public static Set<ValidationException> validate(Object o, Parameter parameter,String name)throws Exception{
        Set<CustomValidator<?,?>> customValidators=new HashSet<>();
        getCustomValidators(parameter.getAnnotations(),customValidators);
        Set<ValidationException> answer=new HashSet<>();
        for (CustomValidator<?,?> customValidator : customValidators) {
            customValidator.validate(o,answer,parameter,name);
        }
        return answer;
    }

    public static Set<ValidationException> validate(Set<CustomValidator<?,?>> validators,Object o)throws Exception{
        Set<ValidationException> answer=new HashSet<>();
        for (CustomValidator<?,?> customValidator : validators) {
            customValidator.validate(o,answer);
        }
        return answer;
    }
}
