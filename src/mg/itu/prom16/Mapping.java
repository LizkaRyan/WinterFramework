package mg.itu.prom16;

public class Mapping {
    Class<?> classe;
    String methodName;
    public Mapping(Class<?> classe,String methodName){
        this.setClasse(classe);
        this.setMethodName(methodName);
    }
    public Class<?> getClasse() {
        return classe;
    }
    public void setClasse(Class<?> classe) {
        this.classe = classe;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
