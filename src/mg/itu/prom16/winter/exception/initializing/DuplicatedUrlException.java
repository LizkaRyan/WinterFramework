package mg.itu.prom16.winter.exception.initializing;

import mg.itu.prom16.winter.exception.WinterException;
import mg.itu.prom16.winter.Mapping;

public class DuplicatedUrlException extends WinterException {
    public DuplicatedUrlException(String url,Mapping mapping1,Mapping mapping2){
        super("L'url :\""+url+"\" revient deux fois:\n"+
        "L'une dans la classe \""+mapping1.getController().getSimpleName()+"\" avec la méthode \""+mapping1.getMethod().getName()+"\"\n"+
        "et l'autre dans la classe \""+mapping2.getController().getSimpleName()+"\" avec la méthode \""+mapping2.getMethod().getName()+"\"");
    }
}
