package mg.itu.prom16.exception;

import javax.servlet.ServletException;

public class WinterException extends ServletException {
    public WinterException(String message){
        super(message);
    }
    public int getStatusCode(){
        return 500;
    }
}
