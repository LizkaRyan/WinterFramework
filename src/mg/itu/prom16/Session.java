package mg.itu.prom16;

import java.util.HashMap;
import java.util.Set;

public class Session {
    protected HashMap<String,Object> map=new HashMap<String,Object>();
    public void add(String key,Object value){
        this.map.put(key, value);
    }
    public void remove(String key){
        this.map.remove(key);
    }
    public Object get(String key){
        return this.map.get(key);
    }
    public void update(String key,String value){
        this.map.replace(key, value);
    }
    public Set<String> getKeys(){
        return this.map.keySet();
    }
}
