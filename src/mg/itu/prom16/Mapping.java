package mg.itu.prom16;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Part;

import mg.itu.prom16.annotation.Attribut;
import mg.itu.prom16.annotation.Param;
import mg.itu.prom16.annotation.RestController;
import mg.itu.prom16.annotation.RestMethod;
import mg.itu.prom16.annotation.WinterFile;
import mg.itu.prom16.exception.ParamNotFoundException;

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
    public Object invokeMethod(HashMap<String,String> requestParameters,HashMap<String,Part> parts,Session session)throws Exception{
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
        return valiny;
    }
    private static void setValue(Object object,HashMap<String,String> requestParameters,String name){
        Field[] fields=object.getClass().getDeclaredFields();
        Set<String> keys=requestParameters.keySet();
        Method[] setters=object.getClass().getMethods();
        for(int i=0;i<fields.length;i++){
            String attribut=fields[i].getName();
            String parameterName=fields[i].getName();
            if(fields[i].isAnnotationPresent(Attribut.class)){
                Attribut attributAnnotation=fields[i].getAnnotation(Attribut.class);
                attribut=attributAnnotation.name();
            }
            for (String key : keys) {
                if(key.contains(name+".")){
                    String attributRequest=key.split("[.]")[1];
                    if(attributRequest.compareTo(attribut)==0){
                        setValue(object, requestParameters.get(key), setters, parameterName);
                    }
                }
            }
        }
    }
    private static Method getSetter(Object object,Method[] setters,String attribut)throws Exception{
        Field[] fields=object.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].isAnnotationPresent(Attribut.class)){
                Attribut attributAnnotation=fields[i].getAnnotation(Attribut.class);
                if(attributAnnotation.name().compareTo(attribut)==0){
                    attribut=fields[i].getName();
                    break;
                }
            }
        }
        String nameSetter="set"+attribut.substring(0,1).toUpperCase()+attribut.substring(1);
        for(int i=0;i<setters.length;i++){
            if(nameSetter.compareToIgnoreCase(setters[i].getName())==0){
                return setters[i];
            }
        }
        throw new Exception("erreur");
    }
    private static void setValue(Object object,String value,Method[] setters,String attribut){
        try {
            Method setter=getSetter(object, setters, attribut);
            Parameter[] parameter=setter.getParameters();
            setter.invoke(object, getPrimitive(parameter[0].getType(),value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}