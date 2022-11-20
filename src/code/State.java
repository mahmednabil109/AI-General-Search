package code;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@interface  StateA{ }

public abstract class State {
    private final Map<String, Object> stateFields = new HashMap<>();

    public void init(State child){
        Class<?> cls = child.getClass();
        for(Field field : cls.getDeclaredFields()){
            try {
                if(field.isAnnotationPresent(StateA.class))
                    this.stateFields.put(field.getName(), field.get(child));
            } catch (IllegalAccessException e) {
                System.out.println("ERROR THROWN " + e.getMessage());
            }
        }
        System.out.println(stateFields);

    }

    public Object getField(String name) {
        return this.stateFields.get(name);
    }

    public State setField(String name, Object value){
        if(this.stateFields.containsKey(name))
            this.stateFields.put(name, value);
        return this;
    }
}
