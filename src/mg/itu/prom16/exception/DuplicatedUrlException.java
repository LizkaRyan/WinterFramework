package mg.itu.prom16.exception;

import mg.itu.prom16.Mapping;

public class DuplicatedUrlException extends Exception {
    public DuplicatedUrlException(String url,Mapping mapping1,Mapping mapping2){
        super("L'url :"+url+" revient deux fois:\n"+
        "L'une dans la classe \""+mapping1.getClasse().getSimpleName()+"\" avec la méthode \""+mapping1.getMethod().getName()+"\"\n"+
        "et l'autre dans la classe \""+mapping2.getClasse().getSimpleName()+"\" avec la méthode \""+mapping2.getMethod().getName()+"\"");
    }
}
