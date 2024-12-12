package framework.setup.model;

import framework.annotations.injection.semantic.Controller;
import framework.exceptions.internal.CreateObjectInternalError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Component {
    private final Class<?> type;
    private final Class<?>[] dependencies;
    private final Factory<?> supplier;
    private Object instance = null;
    private String controllerPath;

    public Object getInstance(){
        return instance;
    }

    public void setInstance(Object instance){
        this.instance = instance;
    }

    public void create(Object... args) {
        if(args.length != dependencies.length) throw CreateObjectInternalError.wrongArgCount(type);
        for(int i = 0; i < dependencies.length; i++){
            if(!(dependencies[i].isInstance(args[i])))
                throw CreateObjectInternalError.wrongArgTypes(type, args[i].getClass(), dependencies[i], i);
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

    public boolean isController(){
        return controllerPath != null;
    }

    public String getControllerPath(){
        return controllerPath;
    }

    private Component(Constructor<?> constructor){
        this(constructor::newInstance, constructor.getParameterTypes(), constructor.getDeclaringClass());
    }

    private Component(Factory<?> supplier, Class<?>[] parameterTypes, Class<?> type){
        this.type = type;
        this.dependencies = parameterTypes;
        this.supplier = supplier;
        if(this.type.isAnnotationPresent(Controller.class)){
            Controller controllerAnnotation = this.type.getAnnotation(Controller.class);
            controllerPath = controllerAnnotation.value();
        }
    }

    public static Component forConstructor(Constructor<?> constructor){
        //Class<?> type = constructor.getDeclaringClass();
        //if(Arrays.stream(type.getMethods()).anyMatch(method -> method.isAnnotationPresent(Timed.class))){
        //    Factory<?> factory = (args)-> TimedInterceptor.instantiateAnnotationInterceptedComponent(type, constructor, args);
        //    return new Component(factory, constructor.getParameterTypes(), type);
        //}
        return new Component(constructor);
    }

    public Component(Method method, Object instance){
        this.type = method.getReturnType();
        this.dependencies = method.getParameterTypes();
        this.supplier = (Object... args)-> method.invoke(instance, args);
        this.controllerPath = null;
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
