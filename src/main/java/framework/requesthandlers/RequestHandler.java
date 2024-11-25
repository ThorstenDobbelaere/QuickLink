package framework.requesthandlers;

import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.OutputMapping;
import framework.annotations.mapping.InputMapping;
import framework.configurables.Stringifier;
import framework.context.QuickLinkContext;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestMappingException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.ResponseEntity;
import framework.requesthandlers.impl.*;
import framework.resolver.model.MappedControllerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class RequestHandler{
    private final String mapping;
    public abstract HttpResponse handle(String input) throws RequestException;

    protected RequestHandler(String mapping) {
        this.mapping = mapping;
    }

    private record MethodInfo(QuickLinkContext context, Annotation annotation, String controllerMapping, Method callback, Object controller){}

    public static RequestHandler createHandlerForMethod(QuickLinkContext context, MappedControllerMethod method) {
        Annotation annotation = method.annotation();
        String controllerMapping = method.controllerMapping();
        Method callback = method.method();
        Object controller = method.controller();
        MethodInfo methodInfo = new MethodInfo(context, annotation, controllerMapping, callback, controller);

        if (annotation instanceof OutputMapping outputMapping) {
            return createOutputRequestHandler(methodInfo, outputMapping);
        }

        if (annotation instanceof InputMapping inputMapping) {
            return createInputRequestHandler(methodInfo, inputMapping);
        }

        if(annotation instanceof IOMapping ioMapping){
            return createIORequestHandler(methodInfo, ioMapping);
        }

        throw new RequestMappingException("Unknown annotation: " + annotation);
    }

    private static RequestHandler createOutputRequestHandler(MethodInfo methodInfo, OutputMapping outputMapping) {
        Class<?> returnType = methodInfo.callback.getReturnType();
        String methodUrl = outputMapping.value();
        String url = methodInfo.controllerMapping + methodUrl;
        Class<? extends Stringifier> stringifierClass = outputMapping.stringifier();
        Stringifier stringifier = methodInfo.context.getInstanceOfType(stringifierClass);

        if(returnType.equals(ResponseEntity.class)) {
            Supplier<ResponseEntity> responseEntitySupplier = ()->{
                try{return (ResponseEntity) methodInfo.callback.invoke(methodInfo.controller);}
                catch(IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };
            return new CustomOutputRequestHandler(url, responseEntitySupplier, stringifier);
        }
        Supplier<Object> resultSupplier = ()-> {
            try {return methodInfo.callback.invoke(methodInfo.controller);}
            catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
        };

        return new OutputRequestHandler(url, resultSupplier, stringifier);
    }

    private static RequestHandler createInputRequestHandler(MethodInfo methodInfo, InputMapping inputMapping) {
        String methodMapping = inputMapping.value();
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback.getParameterTypes()).toList();

        Consumer<Object[]> consumer = (input)-> {
            try {methodInfo.callback.invoke(methodInfo.controller, input);}
            catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
        };
        return new InputRequestHandler(methodInfo.controllerMapping + methodMapping, consumer, requiredTypes);
    }

    private static RequestHandler createIORequestHandler(MethodInfo methodInfo, IOMapping ioMapping) {
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback.getParameterTypes()).toList();
        Class<? extends Stringifier> stringifierClass = ioMapping.stringifier();
        String methodUrl = ioMapping.value();
        Class<?> returnType = methodInfo.callback.getReturnType();
        String url = methodInfo.controllerMapping + methodUrl;
        Stringifier stringifier = methodInfo.context.getInstanceOfType(stringifierClass);

        if(returnType.equals(ResponseEntity.class)) {
            Function<Object[] ,ResponseEntity> responseEntitySupplier = (input)->{
                try{return (ResponseEntity) methodInfo.callback.invoke(methodInfo.controller, input);}
                catch(IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };
            return new CustomIORequestHandler(url, stringifier, responseEntitySupplier, requiredTypes);
        }

        Function<Object[], Object> handler = (input)->{
            try {return methodInfo.callback.invoke(methodInfo.controller, input);}
            catch(IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
        };

        return new IORequestHandler(url, stringifier, handler, requiredTypes);
    }


    public String getMapping() {
        return mapping;
    }
}
