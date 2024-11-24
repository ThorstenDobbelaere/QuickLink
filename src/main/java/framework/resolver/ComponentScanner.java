package framework.resolver;

import framework.annotations.Injectable;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import framework.configurables.impl.DefaultConfigurationMappings;
import framework.context.QuickLinkContext;
import framework.exceptions.scanning.DuplicateException;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.resolver.model.Component;
import org.reflections.Reflections;
import framework.resolver.reflection.AnnotationReflectionHelper;
import framework.resolver.helper.AccessibilityHelper;
import framework.resolver.helper.constructor.ConfigConstructorHelper;
import framework.resolver.helper.constructor.InjectableConstructorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ComponentScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanner.class);

    public static void fillComponentSet(QuickLinkContext context) {
        Reflections reflections = context.getReflectionContext().getProjectReflections();

        Map<Class<?>, Class<? extends Annotation>> injectables = AnnotationReflectionHelper.getTypesAnnotatedWithDirectSubtypes(context, Injectable.class);
        List<Constructor<?>> injectableConstructors = injectables
                .keySet()
                .stream()
                .map(InjectableConstructorFinder::tryGetConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .collect(Collectors.toUnmodifiableList());

        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(Config.class);
        List<Constructor<?>> configDefaultConstructors = configClasses.stream()
                .map(ConfigConstructorHelper::tryFindDefaultConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .collect(Collectors.toUnmodifiableList());

        Map<Class<?>, Object> configObjects = configDefaultConstructors
                .stream()
                .map(Component::new)
                .peek(Component::create)
                .collect(Collectors.toUnmodifiableMap(Component::getType, Component::getInstance));

        List<Method> beanMethods = configClasses.stream()
                .map((config) ->
                        Arrays.stream(config.getDeclaredMethods())
                                .filter(method -> method.isAnnotationPresent(Bean.class))
                                .map(AccessibilityHelper::trySetMethodAccessible)
                                .toList())
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });

        List<Component> beanComponents = beanMethods.stream()
                .map(m-> tryMapBeanToComponent(configObjects, m))
                .toList();

        List<Component> injectableComponents = injectableConstructors.stream()
                .map(Component::new)
                .toList();

        DefaultConfigurationMappings defaultConfigurationMappings = new DefaultConfigurationMappings();
        List<Component> defaultComponents = Arrays.stream(DefaultConfigurationMappings.class
                .getDeclaredMethods())
                .map(m->new Component(m, defaultConfigurationMappings))
                .toList();

        List<Component> components = new ArrayList<>();
        components.addAll(beanComponents);
        components.addAll(injectableComponents);

        List<Component> defaultComponentsToAdd = defaultComponents.stream()
                .filter(entry1 -> components.stream().noneMatch(entry2 -> entry1.getType().equals(entry2.getType())))
                .toList();

        components.addAll(defaultComponentsToAdd);

        List<Component> duplicateComponents = components.stream()
                .filter(entry1->components.stream()
                        .filter(entry2-> entry1.getType().equals(entry2.getType())
                                ).count()>1)
                .distinct()
                .toList();

        if (!duplicateComponents.isEmpty()) {
            throw DuplicateException.duplicateComponent(duplicateComponents.getFirst().getType());
        }

        Set<Component> componentSet = new LinkedHashSet<>(components);
        context.getCache().setComponents(componentSet);

        String message = context.getLogFormatter().highlight("Component scanning complete. Entries are: \n{}");
        LOGGER.debug(message, componentSet.stream()
                .map(component -> String.format("| - %-100s |", component.getType()))
                .collect(Collectors.joining("\n")));
    }



    private static Component tryMapBeanToComponent(Map<Class<?>, Object> configObjects, Method method) {
        Class<?> methodClass = method.getDeclaringClass();
        if(!configObjects.containsKey(methodClass)) {
            throw MapMethodObjectInternalError.configNotFound(methodClass);
        }
        Object value = configObjects.get(methodClass);
        return new Component(method, value);
    }
}
