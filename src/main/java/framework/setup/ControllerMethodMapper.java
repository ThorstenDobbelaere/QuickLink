package framework.setup;

import framework.context.QuickLinkContext;
import framework.request.handlers.factory.HandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class ControllerMethodMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMethodMapper.class);

    public static void mapHandlersForRequests(QuickLinkContext context) {
        var cache = context.getCache();
        var controllers = cache.getMappedControllers();
        var mappedMethods = framework.setup.reflection.ControllerMethodMapper.getMappedMethodsForControllers(context, controllers);
        var requestHandlers = mappedMethods.stream()
                .map(mappedControllerMethod -> HandlerFactory.createHandlerForMethod(context, mappedControllerMethod))
                .toList();

        String message = context.getLogFormatter().highlight("Request mapping complete. Available urls are:\n{}");
        LOGGER.debug(message, requestHandlers.
                stream()
                .map(requestHandler -> String.format("| - %-60s  |", requestHandler.getMappingWithParams()))
                .collect(Collectors.joining("\n")));

        cache.setRequestHandlerList(requestHandlers);
    }
}
