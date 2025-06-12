package component_scan.strategies.implementations;

import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.InjectableScanStrategy;

import java.util.Collection;

public class AnnotationBasedInjectableScanStrategy implements InjectableScanStrategy {
    private final AnnotationReflectionStrategy annotationReflectionStrategy;

    private final AnnotationSet injectableTypes;

    public AnnotationBasedInjectableScanStrategy(
            AnnotationReflectionStrategy annotationReflectionStrategy,
            AnnotationSet injectableTypes) {
        this.annotationReflectionStrategy = annotationReflectionStrategy;
        this.injectableTypes = injectableTypes;
    }

    @Override
    public Collection<InjectableClass<?>> scanInjectableClasses() {
        return annotationReflectionStrategy.getClassesAnnotatedWith(injectableTypes);
    }
}
