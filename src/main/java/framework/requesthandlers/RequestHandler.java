package framework.requesthandlers;

import framework.annotations.mapping.OutputMapping;
import framework.annotations.mapping.InputMapping;
import framework.configurables.Stringifier;
import framework.context.QuickLinkContext;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestMappingException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.ResponseEntity;
import framework.requesthandlers.impl.CustomOutputRequestHandler;
import framework.resolver.model.MappedMethod;
import framework.requesthandlers.impl.OutputRequestHandler;
import framework.requesthandlers.impl.SetRequestHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestHandler{
    private final String mapping;
    public abstract HttpResponse handle(String input) throws RequestException;

    protected RequestHandler(String mapping) {
        this.mapping = mapping;
    }

    public static RequestHandler createHandlerForMethod(QuickLinkContext context, MappedMethod method) {
        Annotation annotation = method.annotation();
        String controllerMapping = method.controllerMapping();
        Method callback = method.method();
        Object controller = method.controller();

        if (annotation instanceof OutputMapping outputMapping) {
            Class<?> returnType = callback.getReturnType();
            String methodMapping = outputMapping.value();
            String mapping = controllerMapping + methodMapping;

            if(returnType.equals(ResponseEntity.class)) {
                Supplier<ResponseEntity> responseEntitySupplier = ()->{
                    try{return (ResponseEntity) callback.invoke(controller);}
                    catch(IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
                };
                return new CustomOutputRequestHandler(mapping, responseEntitySupplier);
            }
            Supplier<Object> resultSupplier = ()-> {
                try {return callback.invoke(controller);}
                catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };

            Class<? extends Stringifier> stringifierClass = outputMapping.stringifier();
            Stringifier stringifier = context.getInstanceOfType(stringifierClass);

            return new OutputRequestHandler(controllerMapping + methodMapping, resultSupplier, stringifier);
        }

        if (annotation instanceof InputMapping inputMapping) {
            String methodMapping = inputMapping.value();
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
