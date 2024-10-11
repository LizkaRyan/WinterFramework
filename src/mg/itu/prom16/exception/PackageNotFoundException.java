package mg.itu.prom16.exception;

public class PackageNotFoundException extends WinterException {
    public PackageNotFoundException(String url){
        super("Le package :\""+url+"\" n'existe pas");
    }   
}
