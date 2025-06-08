package framework.setup;

import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import framework.annotations.interception.Timed;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.context.QuickLinkContext;
import framework.context.config.LogFormatter;
import framework.exceptions.componentscan.DuplicateException;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithTimedMethods;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.DefaultStrategies;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import org.reflections.Reflections;
import framework.setup.helper.AccessibilityHelper;
import framework.setup.helper.constructor.ConfigConstructorHelper;
import framework.setup.helper.constructor.InjectableConstructorFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ComponentScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanner.class);



    private ComponentScanner() {}

    public static void scanComponentsAndInterceptables(QuickLinkContext context) {
        var projectReflections = context.getReflectionContext().getProjectReflections();
        var componentScanStrategy = DefaultStrategies.componentScanStrategy(projectReflections);
        var injectableScanStrategy = DefaultStrategies.injectableScanStrategy(componentScanStrategy);
        Collection<InjectableClass<?>> injectableClasses = injectableScanStrategy.scanInjectableClasses();
        LogFormatter logFormatter = context.getLogFormatter();

        var timedMethods = mapTimedMethods(injectableClasses, componentScanStrategy);
        logTimedMethodScanCompleteMessage(logFormatter, timedMethods);
        context.getCache().setTimedMethods(timedMethods);

        Collection<Component> components = new LinkedHashSet<>();
        components.addAll(scanBeanComponents(context));
        components.addAll(toEmptyComponents(injectableClasses));
        applyDefaultConfigurations(components);
        checkForDuplicates(components);
        context.getCache().setComponents(components);

        logComponentScanCompleteMessage(logFormatter, components);
    }

    private static void logTimedMethodScanCompleteMessage(
            LogFormatter logFormatter,
            List<InjectableClassWithTimedMethods<?>> timedMethods
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

    private static List<Component> scanBeanComponents(QuickLinkContext context) {
        Reflections reflections = context.getReflectionContext().getProjectReflections();

        var configObjects = createObjectMapUsingDefaultConstructor(reflections.getTypesAnnotatedWith(Config.class));

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

    private static Map<Class<?>, Object> createObjectMapUsingDefaultConstructor(Set<Class<?>> classesToMap) {
        UnaryOperator<Component> instantiateWithDefaultConstructor = component -> {
            component.create();
            return component;
        };

        return classesToMap.stream()
                .map(ConfigConstructorHelper::tryFindDefaultConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::forConstructor)
                .map(instantiateWithDefaultConstructor)
                .collect(Collectors.toUnmodifiableMap(Component::getType, Component::getInstance));
    }

    private static List<Component> toEmptyComponents(Collection<InjectableClass<?>> injectableClasses) {
        return injectableClasses
                .stream()
                .map(InjectableConstructorFinder::tryGetConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::forConstructor)
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

    private static List<InjectableClassWithTimedMethods<?>> mapTimedMethods(Collection<InjectableClass<?>> classList, ComponentScanStrategy componentScanStrategy) {
        return classList
                .stream()
                .<InjectableClassWithTimedMethods<?>> map(i -> addTimedMethods(i, componentScanStrategy))
                .filter(injectable -> !injectable.annotatedMethods().isEmpty())
                .toList();
    }

    private static <T> InjectableClassWithTimedMethods<T> addTimedMethods(InjectableClass<T> injectableClass, ComponentScanStrategy componentScanStrategy) {
        Class<T> type = injectableClass.classType();
        var methods = componentScanStrategy.getMethodsAnnotatedWith(type, new AnnotationSet(Set.of(Timed.class)));
        return new InjectableClassWithTimedMethods<>(type, injectableClass.annotationType(), methods);
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
