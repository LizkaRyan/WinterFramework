package mg.itu.prom16.winter.exception.initializing;

import mg.itu.prom16.winter.exception.WinterException;

public class PackageNotFoundException extends WinterException {
    public PackageNotFoundException(String url){
        super("Le package :\""+url+"\" n'existe pas");
    }   
}
