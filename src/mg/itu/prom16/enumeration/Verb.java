package mg.itu.prom16.enumeration;

public enum Verb {
    GET,
    POST;
    public Verb getOther(){
        if(this==GET){
            return POST;
        }
        return GET;
    }
    public String toString(){
        if(this==GET){
            return "GET";
        }
        return "POST";
    }
}