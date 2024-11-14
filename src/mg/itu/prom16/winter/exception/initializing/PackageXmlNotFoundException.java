package mg.itu.prom16.winter.exception.initializing;

import mg.itu.prom16.winter.exception.WinterException;

public class PackageXmlNotFoundException extends  WinterException {
    public PackageXmlNotFoundException(){
        super("Le package des controllers n'est pas définie dans le web.xml");
    }
}
