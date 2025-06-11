package framework.setup.strategies.implementations;

import component_scan.helper.AccessibilityHelper;
import framework.setup.model.reflection.annotated_entities.AnnotatedMethod;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.model.reflection.annotation.AnnotationType;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ReflectionsComponentScanStrategy implements ComponentScanStrategy {
    private final Reflections reflections;
    public ReflectionsComponentScanStrategy(Reflections reflections) {
        this.reflections = reflections;
    }


    @Override
    public Collection<AnnotatedMethod> getMethodsAnnotatedWith(Class<?> type, AnnotationSet annotations) {
        return annotations.annotations().stream()
                .map(a -> getMethodsAnnotatedWith(type, a))
                .reduce(new HashSet<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    private Collection<AnnotatedMethod> getMethodsAnnotatedWith(Class<?> type, AnnotationType annotationType) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationType.annotation()))
                .map(AccessibilityHelper::trySetMethodAccessible)
                .map(m -> new AnnotatedMethod(m, annotationType))
                .toList();
    }

    @Override
    public Collection<InjectableClass<?>> getClassesAnnotatedWith(AnnotationSet annotations) {
        return annotations.annotations().stream()
                .map(this::getClassesAnnotatedWith)
                .reduce(new HashSet<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    private Collection<InjectableClass<?>> getClassesAnnotatedWith(AnnotationType annotation) {
        return reflections.getTypesAnnotatedWith(annotation.annotation()).stream()
                .map(type -> new InjectableClass<>(type, annotation))
                .collect(Collectors.toSet());
    }
}
