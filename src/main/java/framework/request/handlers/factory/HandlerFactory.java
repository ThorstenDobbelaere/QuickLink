package framework.request.handlers.factory;

import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;
import framework.configurables.conversions.OutputConverter;
import framework.context.QuickLinkContext;
import framework.exceptions.request.RequestMappingException;
import framework.request.handlers.MappedRequestHandler;
import framework.request.handlers.impl.CustomIOMappedRequestHandler;
import framework.request.handlers.impl.IOMappedRequestHandler;
import framework.request.handlers.impl.InputMappedRequestHandler;
import framework.request.response.ResponseEntity;
import framework.setup.model.MappedControllerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandlerFactory {

    public static MappedRequestHandler createHandlerForMethod(QuickLinkContext context, MappedControllerMethod method) {
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

    private static MappedRequestHandler createOutputRequestHandler(MethodInfo methodInfo, OutputMapping outputMapping) {
        Class<? extends OutputConverter> outputConverterClass = outputMapping.outputConverter();
        String methodUrl = outputMapping.value();
        return createIORequestHandler(methodInfo, methodUrl, outputConverterClass);
    }

    private static MappedRequestHandler createInputRequestHandler(MethodInfo methodInfo, InputMapping inputMapping) {
        String methodMapping = inputMapping.value();
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback().getParameterTypes()).toList();
        List<String> paramNames = Arrays.stream(methodInfo.callback().getParameters()).map(Parameter::getName).toList();

        Consumer<Object[]> consumer = HandlerMethodFactory.createHandlerConsumerReflect(methodInfo);
        return new InputMappedRequestHandler(methodInfo.controllerMapping() + methodMapping, consumer, requiredTypes, paramNames);
    }

    private static MappedRequestHandler createIORequestHandler(MethodInfo methodInfo, IOMapping ioMapping) {
        Class<? extends OutputConverter> outputConverterClass = ioMapping.outputConverter();
        String methodUrl = ioMapping.value();
        return createIORequestHandler(methodInfo, methodUrl, outputConverterClass);
    }

    private static MappedRequestHandler createIORequestHandler(MethodInfo methodInfo, String methodUrl, Class<? extends OutputConverter> outputConverterClass) {
        List<Class<?>> requiredTypes = Arrays.stream(methodInfo.callback().getParameterTypes()).toList();
        List<String> paramNames = Arrays.stream(methodInfo.callback().getParameters()).map(Parameter::getName).toList();
        Class<?> returnType = methodInfo.callback().getReturnType();
        String url = methodInfo.controllerMapping() + methodUrl;
        OutputConverter outputConverter = methodInfo.context().getInstanceOfType(outputConverterClass);

        if(returnType.equals(ResponseEntity.class)) {
            Function<Object[] ,ResponseEntity> handler = HandlerMethodFactory.createHandlerMethodReflect(methodInfo, ResponseEntity.class);
            return new CustomIOMappedRequestHandler(url, outputConverter, handler, requiredTypes, paramNames);
        }

        Function<Object[], Object> handler = HandlerMethodFactory.createHandlerMethodReflect(methodInfo, Object.class);
        return new IOMappedRequestHandler(url, outputConverter, handler, requiredTypes, paramNames);
    }
}
