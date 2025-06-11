package framework.setup;

import component_scan.annotations.injection.config.Bean;
import component_scan.annotations.injection.config.Config;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.context.config.LogFormatter;
import component_scan.exceptions.DuplicateException;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.DefaultStrategies;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import component_scan.helper.AccessibilityHelper;
import component_scan.helper.ConstructorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ComponentScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanner.class);

    private ComponentScanner() {}

    public static void scanComponentsAndInterceptables(ComponentScanStrategy componentScanStrategy, LogFormatter logFormatter) {
        var injectableScanStrategy = DefaultStrategies.injectableScanStrategy();
        Collection<InjectableClass<?>> injectableClasses = injectableScanStrategy.scanInjectableClasses();

        var timedMethods = DefaultStrategies.interceptMethodScanStrategy().getInterceptedMethods();
        logTimedMethodScanCompleteMessage(logFormatter, timedMethods);

        Collection<Component> components = new LinkedHashSet<>();
        components.addAll(scanBeanComponents(componentScanStrategy));
        components.addAll(toEmptyComponents(injectableClasses));
        applyDefaultConfigurations(components);
        checkForDuplicates(components);

        logComponentScanCompleteMessage(logFormatter, components);
    }

    private static void logTimedMethodScanCompleteMessage(
            LogFormatter logFormatter,
            Collection<InjectableClassWithInterceptedMethods<?>> timedMethods
    ) {
        if (!LOGGER.isDebugEnabled()) return;

        String timedMethodScanCompleteMessage = logFormatter.highlight("Timed method scanning complete. Entries are: \n{}");

        LOGGER.debug(timedMethodScanCompleteMessage, timedMethods.stream()
                .map(injectable -> String.format("| -> %-100s |%n%s", injectable.classType().toString(),
                        injectable.annotatedMethods().stream()
                                .map(method -> String.format("|        %-96s |", method.toString()))
                                .collect(Collectors.joining("\n"))))
                .collect(Collectors.joining("\n")));
    }

    private static void logComponentScanCompleteMessage(LogFormatter logFormatter, Collection<Component> components) {
        if (!LOGGER.isDebugEnabled()) return;

        String componentScanCompleteMessage = logFormatter.highlight("Component scanning complete. Entries are: \n{}");

        LOGGER.debug(componentScanCompleteMessage, components.stream()
                .map(component -> String.format("| - %-100s |", component.getType()))
                .collect(Collectors.joining("\n")));

    }

    private static List<Component> scanBeanComponents(ComponentScanStrategy componentScanStrategy) {
        AnnotationSet annotationSet = new AnnotationSet(Set.of(Config.class));
        var configObjects = instantiateConfigurations(componentScanStrategy.getClassesAnnotatedWith(annotationSet));

        return findBeansForClasses(configObjects.keySet())
                .stream()
                .map(m-> tryMapBeanToComponent(configObjects, m))
                .toList();
    }

    private static void checkForDuplicates(Collection<Component> components) {
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

    private static void applyDefaultConfigurations(Collection<Component> components) {
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

    private static Map<Class<?>, Object> instantiateConfigurations(Collection<InjectableClass<?>> classesToMap) {
        UnaryOperator<Component> instantiateWithDefaultConstructor = component -> {
            component.instantiate();
            return component;
        };

        return classesToMap.stream()
                .map(ConstructorFinder::findDefaultConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::fromConstructor)
                .map(instantiateWithDefaultConstructor)
                .collect(Collectors.toUnmodifiableMap(Component::getType, Component::getInstance));
    }

    private static List<Component> toEmptyComponents(Collection<InjectableClass<?>> injectableClasses) {
        return injectableClasses
                .stream()
                .map(ConstructorFinder::findPrimaryConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::fromConstructor)
                .toList();
    }

    private static List<Method> findBeansForClasses(Collection<Class<?>> classList) {
        return classList.stream()
                .map(ComponentScanner::getMethodsWithAnnotation)
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private static List<Method> getMethodsWithAnnotation(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
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
