package mg.itu.prom16;

import java.lang.reflect.Method;
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
    public Object invokeMethod()throws Exception{
        Constructor<?> constructeur=this.getClasse().getConstructor();
        Object obj=constructeur.newInstance();
        return method.invoke(obj);
    }
}
