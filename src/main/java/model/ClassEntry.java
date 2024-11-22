package model;

import exceptions.internal.CreateObjectInternalError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassEntry{
    private final Class<?> type;
    private final Class<?>[] dependencies;
    private final Factory<?> supplier;
    private Object instance = null;

    public Object getInstance(){
        return instance;
    }

    public void create(Object... args) {
        if(args.length != dependencies.length) throw CreateObjectInternalError.wrongArgCount(type);
        for(int i = 0; i < dependencies.length; i++){
            if(!(dependencies[i].isInstance(args[i]))) throw CreateObjectInternalError.wrongArgTypes(type);
        }

        try{
            this.instance = supplier.create(args);
        } catch (InvocationTargetException e) {
            throw CreateObjectInternalError.invocationException(type, e);
        } catch (InstantiationException e) {
            throw CreateObjectInternalError.newInstanceException(type, e);
        } catch (IllegalAccessException e) {
            throw CreateObjectInternalError.noAccess(type);
        }

    }

    public ClassEntry(Constructor<?> constructor){
        this.type = constructor.getDeclaringClass();
        this.dependencies = constructor.getParameterTypes();
        this.supplier = constructor::newInstance;
    }

    public ClassEntry(Method method, Object instance){
        this.type = method.getReturnType();
        this.dependencies = method.getParameterTypes();
        this.supplier = (Object... args)-> method.invoke(instance, args);
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?>[] getDependencies() {
        return dependencies;
    }

    public boolean isCached(){
        return instance != null;
    }
}
