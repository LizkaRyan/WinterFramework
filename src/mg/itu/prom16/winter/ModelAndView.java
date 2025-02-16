package mg.itu.prom16.winter;
import java.util.HashMap;

public class ModelAndView{
    String url;
    HashMap<String,Object> objects=new HashMap<String,Object>();
    public ModelAndView(String url) {
        this.url = url;
    }
    public ModelAndView() {
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getObjects() {
        return objects;
    }
    public void setObjects(HashMap<String, Object> objects) {
        this.objects = objects;
    }
    public ModelAndView addObject(String key,Object object){
        this.objects.put(key,object);
        return this;
    }
}