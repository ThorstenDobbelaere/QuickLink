package framework.setup;

import framework.context.QuickLinkContext;
import framework.setup.model.MappedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ControllerMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMapper.class);
    public static void mapControllersToUrls(QuickLinkContext context) {
        var cache = context.getCache();
        var componentObjectMap = cache.getComponentObjectMap();

        Set<MappedController> mappedControllerSet = componentObjectMap.entrySet().stream()
                .filter(entry->entry.getKey().isController())
                .map(entry->new MappedController(entry.getKey().getControllerPath(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());

        String controllerMessage = context.getLogFormatter().highlight("Url -> Controller mapping complete. Mappings are: \n{}");
        LOGGER.debug(controllerMessage, mappedControllerSet.stream()
                .map(mappedController -> String.format("| - %-60s ->    %-80s | ", mappedController.mapping(), mappedController.controller()))
                .collect(Collectors.joining("\n")));

        cache.setMappedControllers(mappedControllerSet);
    }
}
