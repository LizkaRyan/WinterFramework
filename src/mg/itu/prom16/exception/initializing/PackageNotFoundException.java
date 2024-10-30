package mg.itu.prom16.exception.initializing;

import mg.itu.prom16.exception.WinterException;

public class PackageNotFoundException extends WinterException {
    public PackageNotFoundException(String url){
        super("Le package :\""+url+"\" n'existe pas");
    }   
}
