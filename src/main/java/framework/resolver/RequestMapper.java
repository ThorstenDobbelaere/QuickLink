package framework.resolver;

import framework.context.QuickLinkContext;
import framework.resolver.model.MappedController;
import framework.resolver.model.MappedControllerMethod;
import framework.resolver.reflection.ControllerMethodMapper;
import framework.requesthandlers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RequestMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMapper.class);

    public static void mapRequests(QuickLinkContext context) {
        var cache = context.getCache();
        Set<MappedController> controllers = cache.getMappedControllers();
        List<MappedControllerMethod> methods = ControllerMethodMapper.map(context, controllers);

        List<RequestHandler> requestHandlerList = methods.stream()
                .map(mappedControllerMethod -> RequestHandler.createHandlerForMethod(context, mappedControllerMethod))
                .toList();

        String message = context.getLogFormatter().highlight("Request mapping complete. Available urls are:\n{}");
        LOGGER.debug(message, requestHandlerList.
                stream()
                .map(requestHandler -> String.format("| - %-60s  |", requestHandler.getMapping()))
                .collect(Collectors.joining("\n")));

        cache.setRequestHandlerList(requestHandlerList);
    }
}
