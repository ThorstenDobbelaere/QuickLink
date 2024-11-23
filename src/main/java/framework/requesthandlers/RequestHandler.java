package framework.requesthandlers;

import framework.annotations.mapping.StringifierMapping;
import framework.annotations.mapping.SetMapping;
import framework.configurables.Stringifier;
import framework.context.QuickLinkContext;
import framework.exceptions.NoSuchComponentException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestMappingException;
import framework.model.Component;
import framework.model.MappedMethod;
import framework.requesthandlers.impl.HtmlRequestHandler;
import framework.requesthandlers.impl.SetRequestHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestHandler<ReturnType> {
    private final String mapping;
    public abstract ReturnType handle(String input) throws RequestException;

    protected RequestHandler(String mapping) {
        this.mapping = mapping;
    }

    public static RequestHandler<?> createHandlerForMethod(QuickLinkContext context, MappedMethod method) {
        Annotation annotation = method.annotation();
        String controllerMapping = method.controllerMapping();
        Method callback = method.method();
        Object controller = method.controller();
        Set<Component> components = context.getCache().getComponents();

        if (annotation instanceof StringifierMapping stringifierMapping) {
            if(!callback.getReturnType().equals(String.class)) throw new RequestMappingException("StringifierMapping must return String");
            Class<? extends Stringifier> stringifierClass = stringifierMapping.stringifier();
            Optional<Component> stringifierComponentOptional = components.stream().filter(c -> c.getType().equals(stringifierClass)).findFirst();
            if(stringifierComponentOptional.isEmpty()) throw new NoSuchComponentException(stringifierClass);
            Stringifier stringifier = (Stringifier) stringifierComponentOptional.get().getInstance();
            String methodMapping = stringifierMapping.value();

            Supplier<Object> supplier = ()-> {
                try {return callback.invoke(controller).toString();}
                catch (IllegalAccessException | InvocationTargetException e) { throw RequestException.invokeException(e); }
            };

            return new HtmlRequestHandler(controllerMapping + methodMapping, supplier, stringifier);
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
