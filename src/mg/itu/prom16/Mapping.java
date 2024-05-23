package mg.itu.prom16;

public class Mapping {
    String classe;
    String methodName;
    public Mapping(String classe,String methodName){
        this.setClasse(classe);
        this.setMethodName(methodName);
    }
    public String getClasse() {
        return classe;
    }
    public void setClasse(String classe) {
        this.classe = classe;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
