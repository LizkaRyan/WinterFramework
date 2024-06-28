package mg.itu.prom16;

import java.util.HashMap;

public class Session {
    protected HashMap<String,Object> map;
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
}
