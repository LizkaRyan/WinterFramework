package mg.itu.prom16.exception;

public class PackageXmlNotFoundException extends  WinterException {
    public PackageXmlNotFoundException(){
        super("Le package des controllers n'est pas définie dans le web.xml");
    }
}
