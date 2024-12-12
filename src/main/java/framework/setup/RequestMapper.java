package framework.setup;

import framework.context.QuickLinkContext;
import framework.request.handlers.factory.HandlerFactory;
import framework.setup.model.MappedController;
import framework.setup.model.MappedControllerMethod;
import framework.setup.reflection.ControllerMethodMapper;
import framework.request.handlers.RequestHandler;
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
                .map(mappedControllerMethod -> HandlerFactory.createHandlerForMethod(context, mappedControllerMethod))
                .toList();

        String message = context.getLogFormatter().highlight("Request mapping complete. Available urls are:\n{}");
        LOGGER.debug(message, requestHandlerList.
                stream()
                .map(requestHandler -> String.format("| - %-60s  |", requestHandler.getMappingWithParams()))
                .collect(Collectors.joining("\n")));

        cache.setRequestHandlerList(requestHandlerList);
    }
}
