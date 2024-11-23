package framework.requesthandlers;

import framework.annotations.mapping.HtmlMapping;
import framework.annotations.mapping.JsonMapping;
import framework.annotations.mapping.SetMapping;
import framework.exceptions.RequestException;
import framework.exceptions.RequestMappingException;
import framework.model.MappedMethod;
import framework.requesthandlers.impl.HtmlRequestHandler;
import framework.requesthandlers.impl.JsonRequestHandler;
import framework.requesthandlers.impl.SetRequestHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestHandler<ReturnType> {
    private final String mapping;
    public abstract ReturnType handle(String input) throws RequestException;

    protected RequestHandler(String mapping) {
        this.mapping = mapping;
    }

    public static RequestHandler<?> createHandlerForMethod(MappedMethod method) {
        Annotation annotation = method.annotation();
        String controllerMapping = method.controllerMapping();
        Method callback = method.method();
        Object controller = method.controller();

        if (annotation instanceof HtmlMapping htmlMapping) {
            if(!callback.getReturnType().equals(String.class)) throw new RequestMappingException("HtmlMapping must return String");
            String methodMapping = htmlMapping.value();

            Supplier<String> supplier = ()-> {
                try {return callback.invoke(controller).toString();}
                catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };

            return new HtmlRequestHandler(controllerMapping + methodMapping, supplier);
        }

        if (annotation instanceof JsonMapping jsonMapping) {
            if(callback.getReturnType().equals(Void.TYPE)) throw new RequestMappingException("JsonMapping must return Object");
            String methodMapping = jsonMapping.value();

            Supplier<Object> supplier = ()-> {
                try {return callback.invoke(controller);}
                catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };

            return new JsonRequestHandler(controllerMapping + methodMapping, supplier);
        }

        if (annotation instanceof SetMapping setMapping) {
            String methodMapping = setMapping.value();
            Consumer<String> consumer = (input)-> {
                try {callback.invoke(controller, input);}
                catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };
            return new SetRequestHandler(controllerMapping + methodMapping, consumer);
        }
        throw new RequestMappingException("Unknown annotation: " + annotation);

    }

    public String getMapping() {
        return mapping;
    }
}
