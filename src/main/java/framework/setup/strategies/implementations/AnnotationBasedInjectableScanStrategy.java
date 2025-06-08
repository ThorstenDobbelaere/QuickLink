package framework.setup.strategies.implementations;

import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.contracts.InjectableScanStrategy;

import java.util.Collection;

public class AnnotationBasedInjectableScanStrategy implements InjectableScanStrategy {
    private final ComponentScanStrategy componentScanStrategy;

    private final AnnotationSet injectableTypes;

    public AnnotationBasedInjectableScanStrategy(
            ComponentScanStrategy componentScanStrategy,
            AnnotationSet injectableTypes) {
        this.componentScanStrategy = componentScanStrategy;
        this.injectableTypes = injectableTypes;
    }

    @Override
    public Collection<InjectableClass<?>> scanInjectableClasses() {
        return componentScanStrategy.getClassesAnnotatedWith(injectableTypes);
    }
}
