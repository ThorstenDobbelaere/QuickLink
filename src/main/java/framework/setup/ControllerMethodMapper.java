package framework.setup;

import framework.context.QuickLinkContext;
import framework.exceptions.componentscan.DuplicateException;
import framework.exceptions.componentscan.MappingException;
import framework.request.handlers.MappedRequestHandler;
import framework.request.handlers.factory.HandlerFactory;
import framework.setup.helper.reflection.ControllerMethodMapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ControllerMethodMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMethodMapper.class);

    public static void mapHandlersForRequests(QuickLinkContext context) {
        var cache = context.getCache();
        var controllers = cache.getMappedControllers();
        var mappedMethods = ControllerMethodMapperHelper.getMappedMethodsForControllers(context, controllers);
        var requestHandlers = mappedMethods.stream()
                .map(mappedControllerMethod -> HandlerFactory.createHandlerForMethod(context, mappedControllerMethod))
                .toList();

        checkDuplicateUrls(requestHandlers);
        checkEmptyUrls(requestHandlers);

        String message = context.getLogFormatter().highlight("Request mapping complete. Available urls are:\n{}");
        LOGGER.debug(message, requestHandlers.
                stream()
                .map(requestHandler -> String.format("| - %-60s  |", requestHandler.getMappingWithParams()))
                .collect(Collectors.joining("\n")));

        cache.setRequestHandlerList(requestHandlers);
    }

    private static void checkDuplicateUrls(List<MappedRequestHandler> handlers){
        Map<String, Long> urlFrequencyMap = handlers.stream()
                .map(MappedRequestHandler::getMapping)
                .collect(Collectors.groupingBy(e->e, Collectors.counting()));

        List<String> duplicateUrls = urlFrequencyMap.entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateUrls.isEmpty()) {
            String url = duplicateUrls.getFirst();

            throw DuplicateException.duplicateMethodUrl(url, urlFrequencyMap.get(url));
        }
    }

    private static void checkEmptyUrls(List<MappedRequestHandler> handlers){
        List<MappedRequestHandler> emptyHandlers = handlers.stream()
                .filter(e -> e.getMapping().isEmpty())
                .toList();

        if(!emptyHandlers.isEmpty()){
            throw MappingException.emptyMapping();
        }
    }
}
