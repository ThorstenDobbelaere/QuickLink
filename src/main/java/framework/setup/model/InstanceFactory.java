package framework.setup.model;

import java.lang.reflect.InvocationTargetException;

public interface InstanceFactory<T>{
    T create(Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
