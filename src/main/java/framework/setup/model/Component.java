package framework.setup.model;

import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.interception.Timed;
import framework.exceptions.internal.CreateObjectInternalError;
import framework.setup.helper.InterceptionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Component {
    private final Class<?> type;
    private final Class<?>[] dependencies;
    private final InstanceFactory<?> instanceFactory;
    private final String controllerPath;
    private Object instance = null;

    public Object getInstance(){
        return instance;
    }

    public synchronized void instantiate(Object... args) {
        if(args.length != dependencies.length) throw CreateObjectInternalError.wrongArgCount(type);
        for(int i = 0; i < dependencies.length; i++){
            if(!(dependencies[i].isInstance(args[i])))
                throw CreateObjectInternalError.wrongArgTypes(type, args[i].getClass(), dependencies[i], i);
        }

        try{
            this.instance = instanceFactory.create(args);
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

    private Component(InstanceFactory<?> instanceFactory, Class<?>[] parameterTypes, Class<?> type){
        this.type = type;
        this.dependencies = parameterTypes;
        this.instanceFactory = instanceFactory;
        if(this.type.isAnnotationPresent(Controller.class)){
            Controller controllerAnnotation = this.type.getAnnotation(Controller.class);
            controllerPath = controllerAnnotation.value();
        } else {
            controllerPath = null;
        }
    }

    public static Component fromConstructor(Constructor<?> constructor){
        Class<?> type = constructor.getDeclaringClass();
        if(Arrays.stream(type.getMethods()).anyMatch(method -> method.isAnnotationPresent(Timed.class))){
            InstanceFactory<?> instanceFactory = args -> InterceptionHelper.instantiateAnnotationInterceptedComponent(type, constructor, args);
            return new Component(instanceFactory, constructor.getParameterTypes(), type);
        }
        return new Component(constructor);
    }

    public Component(Method bean, Object configuration){
        this.type = bean.getReturnType();
        this.dependencies = bean.getParameterTypes();
        this.instanceFactory = (Object... args) -> bean.invoke(configuration, args);
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
