package framework.resolver;

import framework.context.QuickLinkContext;
import framework.exceptions.internal.InternalException;
import framework.resolver.model.Component;
import framework.resolver.model.MappedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapper.class);

    public static void mapObjectsAndControllers(QuickLinkContext context) {
        var cache = context.getCache();
        Set<Component> components = cache.getComponents();

        Map<Class<?>, Component> typeToEntryMap = components.stream()
                .collect(Collectors.toUnmodifiableMap(Component::getType, entry -> entry));

        Map<Component, Object> entryToObjectMap = components.stream()
                .collect(Collectors.toUnmodifiableMap(
                        component -> component,
                        component -> resolve(component.getType(), typeToEntryMap)
                ));

        String objectMessage = context.getLogFormatter().highlight("Completed class to object mapping. Mappings are: \n{}");
        LOGGER.debug(objectMessage, entryToObjectMap.entrySet().stream()
                .map(entry->String.format("| - %-60s ->    %-80s |", entry.getKey().getType(), entry.getValue()))
                .collect(Collectors.joining("\n")));

        Set<MappedController> mappedControllerSet = entryToObjectMap.entrySet().stream()
                .filter(entry->entry.getKey().isController())
                .map(entry->new MappedController(entry.getKey().getControllerPath(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());

        String controllerMessage = context.getLogFormatter().highlight("Completed controller mapping. Available controllers are: \n{}");
        LOGGER.debug(controllerMessage, mappedControllerSet.stream()
                .map(mappedController -> String.format("| - %-60s ->    %-80s | ", mappedController.mapping(), mappedController.controller()))
                .collect(Collectors.joining("\n")));

        cache.setMappedControllers(mappedControllerSet);

    }

    private static Object resolve(Class<?> type, Map<Class<?>, Component> typeToComponentMap) {
        if(!typeToComponentMap.containsKey(type)){
            throw new InternalException("Class " + type + " not found");
        }
        Component component = typeToComponentMap.get(type);
        if(component.isCached())
            return component.getInstance();

        Class<?>[] dependencies = component.getDependencies();
        Object[] dependencyObjects = Arrays.stream(dependencies)
                .map(
                    t->resolve(t, typeToComponentMap)
                ).toList().toArray();
        component.create(dependencyObjects);
        LOGGER.trace("Resolved {}", String.format("%-45s to %-45s", component, type));

        return component.getInstance();
    }
}
