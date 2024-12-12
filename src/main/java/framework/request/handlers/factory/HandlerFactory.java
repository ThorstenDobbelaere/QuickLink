package framework.request.handlers.factory;

import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;
import framework.configurables.OutputConverter;
import framework.context.QuickLinkContext;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestMappingException;
import framework.request.handlers.RequestHandler;
import framework.request.handlers.impl.CustomIORequestHandler;
import framework.request.handlers.impl.IORequestHandler;
import framework.request.handlers.impl.InputRequestHandler;
import framework.request.response.ResponseEntity;
import framework.setup.model.MappedControllerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandlerFactory {

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
        Class<? extends OutputConverter> outputConverterClass = outputMapping.outputConverter();
        String methodUrl = outputMapping.value();
        return createIORequestHandler(methodInfo, methodUrl, outputConverterClass);
    }

    private static RequestHandler createInputRequestHandler(MethodInfo methodInfo, InputMapping inputMapping) {
        String methodMapping = inputMapping.value();
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback().getParameterTypes()).toList();
        List<String> paramNames = Arrays.stream(methodInfo.callback().getParameters()).map(Parameter::getName).toList();

        Consumer<Object[]> consumer = (input)-> {
            try {methodInfo.callback().invoke(methodInfo.controller(), input);}
            catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.requestInvoked(e); }
        };
        return new InputRequestHandler(methodInfo.controllerMapping() + methodMapping, consumer, requiredTypes, paramNames);
    }

    private static RequestHandler createIORequestHandler(MethodInfo methodInfo, IOMapping ioMapping) {
        Class<? extends OutputConverter> outputConverterClass = ioMapping.outputConverter();
        String methodUrl = ioMapping.value();
        return createIORequestHandler(methodInfo, methodUrl, outputConverterClass);
    }

    private static RequestHandler createIORequestHandler(MethodInfo methodInfo, String methodUrl, Class<? extends OutputConverter> outputConverterClass) {
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback().getParameterTypes()).toList();
        List<String> paramNames = Arrays.stream(methodInfo.callback().getParameters()).map(Parameter::getName).toList();
        Class<?> returnType = methodInfo.callback().getReturnType();
        String url = methodInfo.controllerMapping() + methodUrl;
        OutputConverter outputConverter = methodInfo.context().getInstanceOfType(outputConverterClass);

        if(returnType.equals(ResponseEntity.class)) {
            Function<Object[] ,ResponseEntity> responseEntitySupplier = (input)->{
                try{return (ResponseEntity) methodInfo.callback().invoke(methodInfo.controller(), input);}
                catch(IllegalAccessException | InvocationTargetException e) { throw RequestException.requestInvoked(e); }
            };
            return new CustomIORequestHandler(url, outputConverter, responseEntitySupplier, requiredTypes, paramNames);
        }

        Function<Object[], Object> handler = HandlerMethodFactory.createHandlerMethodReflect(methodInfo);
        return new IORequestHandler(url, outputConverter, handler, requiredTypes, paramNames);
    }
}
