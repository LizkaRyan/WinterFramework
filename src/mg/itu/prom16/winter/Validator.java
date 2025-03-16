package mg.itu.prom16.winter;

import mg.itu.prom16.winter.validation.generic.ValidationException;
import mg.itu.prom16.winter.validation.generic.exception.ListValidationException;

import java.util.ArrayList;
import java.util.List;

public class Validator {
    private List<ValidationException> exceptions=new ArrayList<ValidationException>();

    public boolean isValid(){
        return this.exceptions.size()==0;
    }

    void setExceptions(List<ValidationException> exceptions){
        this.exceptions=exceptions;
    }

    void addExceptions(ListValidationException exceptions){
        this.exceptions.addAll(exceptions.getValidations());
    }

    public ModelAndView setError(ModelAndView dispatcher){
        List<String> message=new ArrayList<String>();
        for (int i=0;i<this.getExceptions().size();i++){
            message.add(this.getExceptions().get(i).getMessage());
            dispatcher.addObject(this.getExceptions().get(i).getField(),true);
        }
        dispatcher.addObject("error.messages",message);
        return dispatcher;
    }

    public List<ValidationException> getExceptions(){
        return this.exceptions;
    }
}
