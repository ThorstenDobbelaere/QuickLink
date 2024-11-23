package framework.resolver;

import framework.model.MappedController;
import framework.model.MappedMethod;
import framework.reflection.ControllerMethodMapper;
import framework.requesthandlers.RequestHandler;

import java.util.*;

public class MethodMappings {
    private static List<RequestHandler<?>> handlers;

    public static void init(Set<MappedController> controllers) {
        List<MappedMethod> methods = ControllerMethodMapper.map(controllers);
        handlers = methods.stream()
                .<RequestHandler<?>>map(RequestHandler::createHandlerForMethod)
                .toList();
    }

    public static List<RequestHandler<?>> getHandlers() {
        return handlers;
    }


}
