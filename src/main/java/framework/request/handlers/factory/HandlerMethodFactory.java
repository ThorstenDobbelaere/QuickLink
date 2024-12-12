package framework.request.handlers.factory;

import framework.exceptions.internal.InternalException;
import framework.exceptions.request.RequestException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;

public class HandlerMethodFactory {
    public static <T> Function<Object[], T> createHandlerMethodReflect(MethodInfo methodInfo){
        return (args) -> {
            try{
                Object result = methodInfo.callback().invoke(args);
                return (T) result;
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw RequestException.requestInvoked(e);
            }
        };
    }

    public static Function<Object[], Object> createHandlerMethod(MethodInfo methodInfo) throws NoSuchMethodException, IllegalAccessException {
        Class<?> controllerClass = methodInfo.controller().getClass();
        String methodName = methodInfo.callback().getName();
        Class<?>[] parameterTypes = Arrays.stream(methodInfo.callback().getParameterTypes()).map(HandlerMethodFactory::unbox).toArray(Class[]::new);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Object.class, parameterTypes);
        MethodHandle methodHandle = lookup.findVirtual(controllerClass, methodName, methodType)
                .bindTo(methodInfo.controller());

        return (input) -> {
            try {
                return methodHandle.invoke(input);
            } catch (WrongMethodTypeException e){
                throw new InternalException(e.getMessage());
            } catch (Throwable e) {
                throw RequestException.requestInvoked(e);
            }
        };
    }

    private static Class<?> unbox(Class<?> type) {
        if(type == Integer.class) return int.class;
        if(type == Long.class) return long.class;
        if(type == Double.class) return double.class;
        if(type == Float.class) return float.class;
        if(type == Boolean.class) return boolean.class;
        if(type == Byte.class) return byte.class;
        if(type == Character.class) return char.class;
        if(type == Short.class) return short.class;
        return type;
    }

}
