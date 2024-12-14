package framework.setup;

import framework.annotations.Injectable;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import framework.annotations.interception.Timed;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.context.QuickLinkContext;
import framework.exceptions.componentscan.DuplicateException;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.setup.model.Component;
import org.reflections.Reflections;
import framework.setup.helper.reflection.AnnotationReflectionHelper;
import framework.setup.helper.AccessibilityHelper;
import framework.setup.helper.constructor.ConfigConstructorHelper;
import framework.setup.helper.constructor.InjectableConstructorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ComponentScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanner.class);

    public static void scanComponentsAndInterceptables(QuickLinkContext context) {
        var injectables = AnnotationReflectionHelper.getTypesAnnotatedWithDirectSubtypes(context, Injectable.class).keySet();
        var timedMethods = mapTimedMethods(injectables);

        String timedMethodScanCompleteMessage = context.getLogFormatter().highlight("Timed method scanning complete. Entries are: \n{}");
        LOGGER.debug(timedMethodScanCompleteMessage, timedMethods.entrySet().stream()
                .map(timedClassWithMethodList -> String.format("| -> %-100s |\n%s", timedClassWithMethodList.getKey().toString(),
                        timedClassWithMethodList.getValue().stream()
                                .map(method -> String.format("|        %-96s |", method.toString()))
                                .collect(Collectors.joining("\n"))))
                .collect(Collectors.joining("\n")));

        List<Component> components = new ArrayList<>();
        components.addAll(scanBeanComponents(context));
        components.addAll(toEmptyComponents(injectables));
        applyDefaultConfigurations(components);
        checkForDuplicates(components);

        Set<Component> componentSet = new LinkedHashSet<>(components);
        context.getCache().setComponents(componentSet);
        context.getCache().setTimedMethods(timedMethods);

        String componentScanCompleteMessage = context.getLogFormatter().highlight("Component scanning complete. Entries are: \n{}");
        LOGGER.debug(componentScanCompleteMessage, componentSet.stream()
                .map(component -> String.format("| - %-100s |", component.getType()))
                .collect(Collectors.joining("\n")));


    }

    private static List<Component> scanBeanComponents(QuickLinkContext context) {
        Reflections reflections = context.getReflectionContext().getProjectReflections();

        var configObjects = createObjectMapUsingDefaultConstructor(reflections.getTypesAnnotatedWith(Config.class));

        return findBeansForClasses(configObjects.keySet())
                .stream()
                .map(m-> tryMapBeanToComponent(configObjects, m))
                .toList();
    }

    private static void checkForDuplicates(List<Component> components) {
        List<Component> duplicateComponents = components.stream()
                .filter(entry1->components.stream()
                        .filter(entry2-> entry1.getType().equals(entry2.getType())
                        ).count()>1)
                .distinct()
                .toList();

        if (!duplicateComponents.isEmpty()) {
            throw DuplicateException.duplicateComponent(duplicateComponents.getFirst().getType());
        }
    }

    private static void applyDefaultConfigurations(List<Component> components) {
        DefaultConfigurationMappings defaultConfigurationMappings = new DefaultConfigurationMappings();
        List<Component> defaultComponents = Arrays.stream(DefaultConfigurationMappings.class
                        .getDeclaredMethods())
                .map(m->new Component(m, defaultConfigurationMappings))
                .toList();

        List<Component> defaultComponentsToAdd = defaultComponents.stream()
                .filter(entry1 -> components.stream().noneMatch(entry2 -> entry1.getType().equals(entry2.getType())))
                .toList();

        components.addAll(defaultComponentsToAdd);
    }

    private static Map<Class<?>, Object> createObjectMapUsingDefaultConstructor(Set<Class<?>> classesToMap) {
        return classesToMap.stream()
                .map(ConfigConstructorHelper::tryFindDefaultConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::forConstructor)
                .peek(Component::create)
                .collect(Collectors.toUnmodifiableMap(Component::getType, Component::getInstance));
    }

    private static List<Component> toEmptyComponents(Set<Class<?>> types) {
        return types
                .stream()
                .map(InjectableConstructorFinder::tryGetConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::forConstructor)
                .toList();
    }

    private static List<Method> findBeansForClasses(Collection<Class<?>> classList) {
        return classList.stream()
                .map(type -> getMethodsWithAnnotation(type, Bean.class))
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private static Map<Class<?>, List<Method>> mapTimedMethods(Collection<Class<?>> classList){
        return classList.stream()
                .collect(Collectors.toUnmodifiableMap(type->type, type -> getMethodsWithAnnotation(type, Timed.class)))
                .entrySet()
                .stream()
                .filter(entry-> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static List<Method> getMethodsWithAnnotation(Class<?> type, Class<? extends Annotation> annotationType) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationType))
                .map(AccessibilityHelper::trySetMethodAccessible)
                .toList();
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
