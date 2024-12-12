package framework.setup;

import framework.context.QuickLinkContext;
import framework.exceptions.internal.InternalException;
import framework.setup.model.Component;
import framework.setup.model.MappedController;
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

        Map<Class<?>, Component> typeToComponentMap = components.stream()
                .collect(Collectors.toUnmodifiableMap(Component::getType, entry -> entry));

        Map<Component, Object> entryToObjectMap = components.stream()
                .collect(Collectors.toUnmodifiableMap(
                        component -> component,
                        component -> resolve(component.getType(), typeToComponentMap)
                ));

        String objectMessage = context.getLogFormatter().highlight("Class -> object mapping complete. Mappings are: \n{}");
        LOGGER.debug(objectMessage, entryToObjectMap.entrySet().stream()
                .map(entry->String.format("| - %-60s ->    %-80s |", entry.getKey().getType(), entry.getValue()))
                .collect(Collectors.joining("\n")));

        Set<MappedController> mappedControllerSet = entryToObjectMap.entrySet().stream()
                .filter(entry->entry.getKey().isController())
                .map(entry->new MappedController(entry.getKey().getControllerPath(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());

        String controllerMessage = context.getLogFormatter().highlight("Url -> Controller mapping complete. Mappings are: \n{}");
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

        instantiateComponent(component, typeToComponentMap);
        return component.getInstance();
    }

    private static void instantiateComponent(Component component, Map<Class<?>, Component> typeToComponentMap) {
        Class<?>[] dependencies = component.getDependencies();
        Class<?> type = component.getType();

        Object[] dependencyObjects = Arrays.stream(dependencies)
                .map(
                        t->resolve(t, typeToComponentMap)
                ).toList().toArray();
        instantiateComponent(component, dependencyObjects);
        LOGGER.trace("Resolved {}", String.format("%-45s to %-45s", component, type));
    }

    private static void instantiateComponent(Component component, Object[] dependencies){
        component.create(dependencies);
    }
}
