package mg.itu.prom16;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        List<String> parametersValue=new ArrayList<String>();
        for(int i=0;i<functionParameters.length;i++){
            parametersValue.add(getParameterValue(requestParameters,functionParameters[i]));
        }
        Object[] parameterValues=parametersValue.toArray();
        return method.invoke(obj,parameterValues);
    }
    private String getParameterValue(HashMap<String,String> requestParameters,Parameter functionParameter){
        if(functionParameter.isAnnotationPresent(Param.class)){
            Param param=functionParameter.getAnnotation(Param.class);
            return requestParameters.get(param.name());
        }
        return requestParameters.get(functionParameter.getName());
    }
}
