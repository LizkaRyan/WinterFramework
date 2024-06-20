package mg.itu.prom16;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mg.itu.prom16.annotation.Param;

import java.lang.reflect.Constructor;

public class Mapping {
    Class<?> classe;
    Method method;
    public Mapping(Class<?> classe,Method method){
        this.setClasse(classe);
        this.setMethod(method);
    }
    public Class<?> getClasse() {
        return classe;
    }
    public void setClasse(Class<?> classe) {
        this.classe = classe;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method methodName) {
        this.method = methodName;
    }
    public Object invokeMethod(HashMap<String,String> requestParameters)throws Exception{
        Constructor<?> constructeur=this.getClasse().getConstructor();
        Object obj=constructeur.newInstance();
        Parameter[] functionParameters=this.method.getParameters();
        List<Object> parametersValue=new ArrayList<Object>();
        for(int i=0;i<functionParameters.length;i++){
            parametersValue.add(getParameterValue(requestParameters,functionParameters[i]));
        }
        Object[] parameterValues=parametersValue.toArray();
        return method.invoke(obj,parameterValues);
    }
    private static boolean isPrimitive(Class<?> classe){
        if(classe==Integer.class){
            return true;
        }
        if(classe==Double.class){
            return true;
        }
        if(classe==Float.class){
            return true;
        }
        if(classe==Long.class){
            return true;
        }
        return false;
    }
    private Object getParameterValue(HashMap<String,String> requestParameters,Parameter functionParameter) throws Exception{
        Class<?> classe=functionParameter.getType();
        if(isPrimitive(classe)){
            if(functionParameter.isAnnotationPresent(Param.class)){
                Param param=functionParameter.getAnnotation(Param.class);
                return requestParameters.get(param.name());
            }
            return requestParameters.get(functionParameter.getName());
        }
        String name=functionParameter.getName();
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
        Set<String> keys=requestParameters.keySet();
        Method[] setters=object.getClass().getMethods();
        for (String key : keys) {
            if(key.contains(name+".")){
                String attribut=name.split(".")[1];
                setValue(object, requestParameters.get(key), setters, attribut);
            }
        }
    }
    private static Method getSetter(Object object,Object value,Method[] setters,String attribut)throws Exception{
        String nameSetter="set"+attribut.substring(0,1).toLowerCase()+attribut.substring(1);
        for(int i=0;i<setters.length;i++){
            if(setters[i].getReturnType()==Void.class && nameSetter==setters[i].getName()){
                return setters[i];
            }
        }
        throw new Exception("erreur");
    }
    private static void setValue(Object object,Object value,Method[] setters,String attribut){
        try {
            Method setter=getSetter(object, value, setters, attribut);
            setter.invoke(object, value);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
