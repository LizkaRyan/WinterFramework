package mg.itu.prom16.winter;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.Part;

import mg.itu.prom16.winter.annotation.field.Attribut;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.method.RestMethod;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.parameter.WinterFile;
import mg.itu.prom16.winter.annotation.type.Controller;
import mg.itu.prom16.winter.annotation.type.RestController;
import mg.itu.prom16.winter.authentication.Authenticate;
import mg.itu.prom16.winter.authentication.Authenticator;
import mg.itu.prom16.winter.exception.running.ParamInjectionNotFoundException;
import mg.itu.prom16.winter.exception.running.ParamNotFoundException;
import mg.itu.prom16.winter.validation.generic.ValidatorUtil;
import mg.itu.prom16.winter.validation.generic.exception.ListValidationException;
import mg.itu.prom16.winter.validation.generic.ValidationException;
import mg.itu.prom16.winter.enumeration.Verb;

public class Mapping {
    Class<?> controller;
    Method method;

    public Mapping(Class<?> classe, Method method) {
        this.setController(classe);
        this.setMethod(method);
    }

    public Verb getVerb(){
        if(this.method.isAnnotationPresent(Get.class)){
            return Verb.GET;
        }
        return Verb.POST;
    }

    public Class<?> getController() {
        return controller;
    }

    public void setController(Class<?> classe) {
        this.controller = classe;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method methodName) {
        this.method = methodName;
    }

    public static String dropFirtSlash(String url) {
        if (url.length() == 0) {
            return url;
        }
        if (url.charAt(0) == '/') {
            return url.substring(1);
        }
        return url;
    }

    public boolean isAController() {
        return this.method.isAnnotationPresent(Post.class) || this.method.isAnnotationPresent(Get.class);
    }

    public static String addFirtSlash(String url) {
        if (url.length() == 0) {
            return "";
        }
        if (url.charAt(0) != '/') {
            return "/" + url;
        }
        return url;
    }

    public String getUrl() {
        String url = "";
        if (controller.isAnnotationPresent(RestController.class)) {
            RestController restController = this.controller.getAnnotation(RestController.class);
            url = dropFirtSlash(restController.mapping());
        } else {
            Controller controller = this.controller.getAnnotation(Controller.class);
            url = dropFirtSlash(controller.mapping());
        }
        if (this.method.isAnnotationPresent(Post.class)) {
            Post post = this.method.getAnnotation(Post.class);
            return url + addFirtSlash(post.value());
        }
        Get get = this.method.getAnnotation(Get.class);
        return url + addFirtSlash(get.value());
    }

    public boolean isRest() {
        return controller.isAnnotationPresent(RestController.class) || method.isAnnotationPresent(RestMethod.class);
    }

    private String[] getParameterName() throws Exception {
        Parameter[] parameter = this.method.getParameters();
        String[] valiny = new String[parameter.length];
        for (int i = 0; i < parameter.length; i++) {
            valiny[i] = parameter[i].getName();
        }
        return valiny;
    }

    public Authenticate getAuthentication(Class<?> authentification) {
        if (this.getMethod().isAnnotationPresent(Authenticate.class)) {
            return this.getMethod().getAnnotation(Authenticate.class);
        }
        if (this.getController().isAnnotationPresent(Authenticate.class)) {
            return this.getController().getAnnotation(Authenticate.class);
        }
        return null;
    }


    public void authenticate(Session session,String url) throws Exception {
        Authenticate authenticate = this.getAuthentication(Authenticate.class);
        if (authenticate == null) {
            return;
        }
        Constructor<? extends Authenticator> constructor = (Constructor<? extends Authenticator>) authenticate.value().getConstructors()[0];
        Class<?>[] classesParameter = constructor.getParameterTypes();
        Object[] parameter = new Object[classesParameter.length];
        for (int i = 0; i < classesParameter.length; i++) {
            if (classesParameter[i] == Session.class) {
                parameter[i] = session;
            } else if (classesParameter[i] == String.class) {
                parameter[i] = url;
            } else {
                throw new ParamInjectionNotFoundException();
            }
        }
        Authenticator authenticator = constructor.newInstance(parameter);
        authenticator.authentificate();
    }

    public Object invokeMethod(Map<String, Object> requestParameters, HashMap<String, Part> parts, Session session,String url) throws Exception {
        this.authenticate(session,url);
        Object obj = this.getControllerInstance(session);
        String[] parameterNames = this.getParameterName();
        Parameter[] functionParameters = this.method.getParameters();
        List<Object> parametersValue = new ArrayList<Object>();
        Validator validator=new Validator();
        for (int i = 0; i < functionParameters.length; i++) {
            parametersValue.add(getParameterValue(requestParameters, parts, functionParameters[i], parameterNames[i], session));
            ListValidationException validationExceptions= (ListValidationException) session.get("winter.validation");
            if(validationExceptions!=null){
                validator.addExceptions(validationExceptions);
                session.remove("winter.validation");
            }
        }
        for (int i = 0; i < functionParameters.length; i++) {
            if(functionParameters[i].getType()==Validator.class){
                parametersValue.remove(i);
                parametersValue.add(i,validator);
            }
        }
        Object[] parameterValues = parametersValue.toArray();
        return method.invoke(obj, parameterValues);
    }

    public Object getControllerInstance(Session session) throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Constructor<?>[] constructeur = this.getController().getConstructors();
        Parameter[] parameters = constructeur[0].getParameters();
        Object[] parameterValue = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == Session.class) {
                parameterValue[i] = session;
            } else {
                parameterValue[i] = null;
            }
        }
        return constructeur[0].newInstance(parameterValue);
    }

    private Object getParameterValue(Map<String, Object> requestParameters, HashMap<String, Part> parts, Parameter functionParameter, String nameParameter, Session session) throws Exception {
        Class<?> classe = functionParameter.getType();
        if(classe==Validator.class){
            return null;
        }
        if (classe.isPrimitive() || classe == String.class) {
            if (functionParameter.isAnnotationPresent(Param.class)) {
                Param param = functionParameter.getAnnotation(Param.class);
                Object value=getStringValueByClass(functionParameter.getType(), (String) requestParameters.get(param.name()));
                Set<ValidationException> lists= ValidatorUtil.validate(value,functionParameter,param.name());
                if (lists.size() != 0) {
                    throw new ListValidationException(lists, value, param.name());
                }
                return value;
            }
            throw new ParamNotFoundException();
        } else if (classe == Session.class) {
            return session;
        } else if (classe == Map.class){
            return requestParameters;
        }
        if (functionParameter.isAnnotationPresent(WinterFile.class)) {
            WinterFile winterFile = functionParameter.getAnnotation(WinterFile.class);
            return parts.get(winterFile.name());
        }
        String name = nameParameter;
        if (functionParameter.isAnnotationPresent(Param.class)) {
            Param param = functionParameter.getAnnotation(Param.class);
            name = param.name();
        }
        Constructor<?> constructor = classe.getConstructor();
        Object valiny = constructor.newInstance();
        if(requestParameters.containsKey(name)){
            setValue(valiny, (Map<String,Object>)requestParameters.get(name));
        }
        Set<ValidationException> validationException = ValidatorUtil.validate(valiny);
        validationException.addAll(ValidatorUtil.validate(valiny,functionParameter,name));
        if (validationException.size() != 0) {
            session.add("winter.validation",new ListValidationException(validationException, valiny, name));
        }
        return valiny;
    }

    private static void setValue(Object object, Map<String, Object> requestParameters) {
        Field[] fields = object.getClass().getDeclaredFields();
        Set<String> keys = requestParameters.keySet();
        Method[] setters = object.getClass().getMethods();
        for (int i = 0; i < fields.length; i++) {
            String attribut = fields[i].getName();
            if (fields[i].isAnnotationPresent(Attribut.class)) {
                Attribut attributAnnotation = fields[i].getAnnotation(Attribut.class);
                attribut = attributAnnotation.name();
            }
            for (String key : keys) {
                if (key.equals(attribut)) {
                    setValue(object, requestParameters.get(key), setters, fields[i]);
                }
            }
        }
    }

    private static Method getSetter(Method[] setters, Field champ) throws Exception {
        String attribut = champ.getName();
        String nameSetter = "set" + attribut.substring(0, 1).toUpperCase() + attribut.substring(1);
        for (int i = 0; i < setters.length; i++) {
            if (nameSetter.compareToIgnoreCase(setters[i].getName()) == 0) {
                return setters[i];
            }
        }
        throw new Exception("erreur");
    }

    private static void setValue(Object object, Object value, Method[] setters, Field attribut) {
        try {
            Method setter = getSetter(setters, attribut);
            Parameter[] parameter = setter.getParameters();
            Object valeur = getStringValueByClass(parameter[0].getType(), value);
            setter.invoke(object, valeur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getStringValueByClass(Class<?> classe, Object string) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (classe == int.class) {
            return Integer.parseInt((String) string);
        }
        else if (classe == double.class) {
            return Double.parseDouble((String) string);
        }
        else if (classe == float.class) {
            return Float.parseFloat((String) string);
        }
        else if (classe == Long.class) {
            return Long.parseLong((String) string);
        }
        else if (classe == LocalDate.class) {
            return LocalDate.parse((String) string);
        }
        else if (classe == LocalDateTime.class) {
            return LocalDateTime.parse((String) string);
        }
        else if (classe == String.class){
            return string;
        }
        else {
            Constructor<?> constructor = classe.getConstructor();
            Object valiny = constructor.newInstance();
            setValue(valiny, (Map<String, Object>) string);
            return valiny;
        }
    }
}