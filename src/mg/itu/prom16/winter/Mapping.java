package mg.itu.prom16.winter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.servlet.http.Part;

import mg.itu.prom16.annotation.field.Attribut;
import mg.itu.prom16.annotation.method.RestMethod;
import mg.itu.prom16.annotation.method.Url;
import mg.itu.prom16.annotation.parameter.Param;
import mg.itu.prom16.annotation.parameter.WinterFile;
import mg.itu.prom16.annotation.type.Controller;
import mg.itu.prom16.annotation.type.RestController;
import mg.itu.prom16.winter.authentification.Authentificate;
import mg.itu.prom16.winter.authentification.Authentificator;
import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.exception.running.ParamInjectionNotFoundException;
import mg.itu.prom16.winter.exception.running.ParamNotFoundException;
import mg.itu.prom16.winter.validation.generic.Validator;
import mg.itu.prom16.winter.validation.generic.exception.ListValidationException;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Mapping {
    Class<?> controller;
    Method method;
    public Mapping(Class<?> classe,Method method){
        this.setController(classe);
        this.setMethod(method);
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
    public static String dropFirtSlash(String url){
        if(url.charAt(0)=='/'){
            return url.substring(1);
        }
        return url;
    }
    public static String addFirtSlash(String url){
        if(url.charAt(0)!='/'){
            return "/"+url;
        }
        return url;
    }
    public String getUrl(){
        String url="";
        if(controller.isAnnotationPresent(RestController.class)){
            RestController restController=this.controller.getAnnotation(RestController.class);
            url=dropFirtSlash(restController.mapping());
        }
        else{
            Controller controller=this.controller.getAnnotation(Controller.class);
            url=dropFirtSlash(controller.mapping());
        }
        Url urlMethod=this.method.getAnnotation(Url.class);
        if(url==""){
            return urlMethod.url();
        }
        return url+addFirtSlash(urlMethod.url());
    }
    public boolean isRest(){
        return controller.isAnnotationPresent(RestController.class) || method.isAnnotationPresent(RestMethod.class);
    }
    private String[] getParameterName()throws Exception{
        Parameter[] parameter=this.method.getParameters();
        String[] valiny=new String[parameter.length];
        for(int i=0;i<parameter.length;i++){
            valiny[i]=parameter[i].getName();
        }
        return valiny;
    }
    public void authentificate(Session session)throws Exception{
        Authentificate authentificate=this.getMethod().getAnnotation(Authentificate.class);
        Constructor<? extends Authentificator> constructor=(Constructor<? extends Authentificator>)authentificate.value().getConstructors()[0];
        Class<?>[] classesParameter=constructor.getParameterTypes();
        Object[] parameter=new Object[classesParameter.length];
        for (int i=0;i<classesParameter.length;i++) {
            if(classesParameter[i]==Session.class){
                parameter[i]=session;
            }
            else{
                throw new ParamInjectionNotFoundException();
            }
        }
        Authentificator authentificator=constructor.newInstance(parameter);
        authentificator.authentificate();
    }
    public Object invokeMethod(HashMap<String,String> requestParameters,HashMap<String,Part> parts,Session session)throws Exception{
        if(this.getMethod().isAnnotationPresent(Authentificate.class)){
            this.authentificate(session);
        }
        Constructor<?> constructeur=this.getController().getConstructor();
        Object obj=constructeur.newInstance();
        Field[] field = this.getController().getDeclaredFields();
        for(int i=0;i<field.length;i++){
            if(field[i].getType()==Session.class){
                field[i].setAccessible(true);
                field[i].set(obj, session);
            }
        }
        String[] parameterNames = this.getParameterName();
        Parameter[] functionParameters=this.method.getParameters();
        List<Object> parametersValue=new ArrayList<Object>();
        for(int i=0;i<functionParameters.length;i++){
            parametersValue.add(getParameterValue(requestParameters,parts,functionParameters[i],parameterNames[i],session));
        }
        Object[] parameterValues=parametersValue.toArray();
        return method.invoke(obj,parameterValues);
    }
    private static Object getPrimitive(Class<?> classe,String string){
        if(classe==int.class){
            return Integer.parseInt(string);
        }
        if(classe==double.class){
            return Double.parseDouble(string);
        }
        if(classe==float.class){
            return Float.parseFloat(string);
        }
        if(classe==Long.class){
            return Long.parseLong(string);
        }
        return string;
    }
    private Object getParameterValue(HashMap<String,String> requestParameters,HashMap<String,Part> parts,Parameter functionParameter,String nameParameter,Session session) throws Exception{
        Class<?> classe=functionParameter.getType();
        if(classe.isPrimitive() || classe==String.class){
            if(functionParameter.isAnnotationPresent(Param.class)){
                Param param=functionParameter.getAnnotation(Param.class);
                return getPrimitive(functionParameter.getType(), requestParameters.get(param.name()));
            }
            throw new ParamNotFoundException();
        }
        else if(classe==Session.class){
            return session;
        }
        if(functionParameter.isAnnotationPresent(WinterFile.class)){
            WinterFile winterFile=functionParameter.getAnnotation(WinterFile.class);
            return parts.get(winterFile.name());
        }
        String name=nameParameter;
        if(functionParameter.isAnnotationPresent(Param.class)){
            Param param=functionParameter.getAnnotation(Param.class);
            name=param.name();
        }
        Constructor<?> constructor=classe.getConstructor();
        Object valiny=constructor.newInstance();
        setValue(valiny, requestParameters, name);
        List<ValidationException> validationException=Validator.validate(valiny);
        if(validationException.size()!=0){
            throw new ListValidationException(validationException,valiny,name);
        }
        return valiny;
    }
    private static void setValue(Object object,HashMap<String,String> requestParameters,String name){
        Field[] fields=object.getClass().getDeclaredFields();
        Set<String> keys=requestParameters.keySet();
        Method[] setters=object.getClass().getMethods();
        for(int i=0;i<fields.length;i++){
            String attribut=fields[i].getName();
            if(fields[i].isAnnotationPresent(Attribut.class)){
                Attribut attributAnnotation=fields[i].getAnnotation(Attribut.class);
                attribut=attributAnnotation.name();
            }
            for (String key : keys) {
                if(key.contains(name+".")){
                    String attributRequest=key.split("[.]")[1];
                    if(attributRequest.compareTo(attribut)==0){
                        setValue(object, requestParameters.get(key), setters, fields[i]);
                    }
                }
            }
        }
    }
    private static Method getSetter(Object object,Method[] setters,Field champ)throws Exception{
        String attribut=champ.getName();
        String nameSetter="set"+attribut.substring(0,1).toUpperCase()+attribut.substring(1);
        for(int i=0;i<setters.length;i++){
            if(nameSetter.compareToIgnoreCase(setters[i].getName())==0){
                return setters[i];
            }
        }
        throw new Exception("erreur");
    }
    private static void setValue(Object object,String value,Method[] setters,Field attribut){
        try {
            Method setter=getSetter(object, setters, attribut);
            Parameter[] parameter=setter.getParameters();
            Object valeur=getPrimitive(parameter[0].getType(),value);
            setter.invoke(object, valeur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}