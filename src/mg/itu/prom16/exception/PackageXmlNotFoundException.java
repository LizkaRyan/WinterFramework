package mg.itu.prom16.exception;

import javax.servlet.ServletException;

public class PackageXmlNotFoundException extends ServletException {
    public PackageXmlNotFoundException(){
        super("Le package des controllers n'est pas définie dans le web.xml");
    }
}
