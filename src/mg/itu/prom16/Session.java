package mg.itu.prom16;

import jakarta.servlet.http.HttpSession;

public class Session {
    protected HttpSession session;
    public Session(){
        
    }
    public Session(HttpSession session){
        this.session=session;
    }
    public void setSession(HttpSession session){
        this.session=session;
    }
    public void add(String key,Object value){
        this.session.setAttribute(key, value);
    }
    public void remove(String key){
        this.session.removeAttribute(key);
    }
    public Object get(String key){
        return this.session.getAttribute(key);
    }
    public void update(String key,String value){
        this.session.setAttribute(key, value);
    }
}
