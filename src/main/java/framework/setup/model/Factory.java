package framework.setup.model;

import java.lang.reflect.InvocationTargetException;

public interface Factory<T>{
    T create(Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
