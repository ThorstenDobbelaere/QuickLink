package framework.request.handlers.factory;

import framework.exceptions.internal.InternalException;
import framework.exceptions.request.RequestException;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandlerMethodFactory {
    public static <T> Function<Object[], T> createHandlerMethodReflect(MethodInfo methodInfo, Class<T> returnType) {
        return (args) -> {
            Object result;
            try{
                result = methodInfo.callback().invoke(methodInfo.controller(), args);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw RequestException.requestInvoked(e);
            }
            if(returnType.isInstance(result)) {
                return returnType.cast(result);
            }
            throw new InternalException("Wrong return type");
        };
    }

    public static Consumer<Object[]> createHandlerConsumerReflect(MethodInfo methodInfo) {
        return (args) -> {
            try{
                methodInfo.callback().invoke(args);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw RequestException.requestInvoked(e);
            }
        };
    }

}
