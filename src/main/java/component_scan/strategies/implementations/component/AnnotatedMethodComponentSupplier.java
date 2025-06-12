package component_scan.strategies.implementations.component;

import helper.AccessibilityHelper;
import helper.ConstructorFinder;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.AnnotatedMethod;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.ComponentSupplier;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class AnnotatedMethodComponentSupplier implements ComponentSupplier {
    private final AnnotationSet classAnnotations;
    private final AnnotationSet methodAnnotations;
    private final AnnotationReflectionStrategy annotationReflectionStrategy;
    private final ConstructorFinder constructorFinder;

    public AnnotatedMethodComponentSupplier(
            AnnotationSet classAnnotations,
            AnnotationSet methodAnnotations,
            AnnotationReflectionStrategy annotationReflectionStrategy, ConstructorFinder constructorFinder
    ) {
        this.classAnnotations = classAnnotations;
        this.methodAnnotations = methodAnnotations;
        this.annotationReflectionStrategy = annotationReflectionStrategy;
        this.constructorFinder = constructorFinder;
    }

    @Override
    public Collection<Component> getComponents() {
        return scanBeanComponents();
    }

    private List<Component> scanBeanComponents() {
        var configObjects = instantiateConfigurations(annotationReflectionStrategy.getClassesAnnotatedWith(classAnnotations));

        return findBeansForClasses(configObjects.keySet())
                .stream()
                .map(m-> mapBeanToComponent(configObjects, m))
                .toList();
    }

    private Map<Class<?>, Object> instantiateConfigurations(Collection<InjectableClass<?>> classesToMap) {
        UnaryOperator<Component> instantiateWithDefaultConstructor = component -> {
            component.instantiate();
            return component;
        };

        return classesToMap.stream()
                .map(constructorFinder::findDefaultConstructor)
                .map(AccessibilityHelper::setConstructorAccessible)
                .map(Component::fromConstructor)
                .map(instantiateWithDefaultConstructor)
                .collect(Collectors.toUnmodifiableMap(Component::getType, Component::getInstance));
    }

    private List<Method> findBeansForClasses(Collection<Class<?>> classList) {
        return classList.stream()
                .map(this::getBeanMethods)
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private List<Method> getBeanMethods(Class<?> config) {
        return annotationReflectionStrategy.getMethodsAnnotatedWith(config, methodAnnotations)
                .stream()
                .map(AnnotatedMethod::method)
                .map(AccessibilityHelper::setMethodAccessible)
                .toList();
    }

    private static Component mapBeanToComponent(Map<Class<?>, Object> configInstances, Method bean) {
        Class<?> configClass = bean.getDeclaringClass();
        if(!configInstances.containsKey(configClass)) {
            throw MapMethodObjectInternalError.configNotFound(configClass);
        }
        Object value = configInstances.get(configClass);
        return new Component(bean, value);
    }
}
