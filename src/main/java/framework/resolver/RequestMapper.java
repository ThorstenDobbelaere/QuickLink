package framework.resolver;

import framework.context.QuickLinkContext;
import framework.model.MappedController;
import framework.model.MappedMethod;
import framework.reflection.ControllerMethodMapper;
import framework.requesthandlers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RequestMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMapper.class);

    public static void init(QuickLinkContext context) {
        var cache = context.getCache();
        Set<MappedController> controllers = cache.getMappedControllers();
        List<MappedMethod> methods = ControllerMethodMapper.map(context, controllers);
        cache.setMappedMethods(new HashSet<>(methods));

        List<RequestHandler<?>> requestHandlerList = methods.stream()
                .<RequestHandler<?>>map(mappedMethod -> RequestHandler.createHandlerForMethod(context, mappedMethod))
                .toList();

        LOGGER.info("Request mapping complete. Available mappings are:\n{}", requestHandlerList.
                stream()
                .map(requestHandler -> String.format("| - %-60s  |", requestHandler.getMapping()))
                .collect(Collectors.joining("\n")));

        cache.setRequestHandlerList(requestHandlerList);
    }
}
