package mg.itu.prom16.exception;

import jakarta.servlet.ServletException;

public class PackageNotFoundException extends ServletException {
    public PackageNotFoundException(String url){
        super("Le package :\""+url+"\" n'existe pas");
    }
}
