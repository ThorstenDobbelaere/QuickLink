package component_scan.strategies.implementations.component;

import helper.AccessibilityHelper;
import helper.ConstructorFinder;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import component_scan.strategies.contracts.ComponentSupplier;
import component_scan.strategies.contracts.InjectableScanStrategy;

import java.util.Collection;
import java.util.List;

public class AnnotatedClassComponentSupplier implements ComponentSupplier {
    private final InjectableScanStrategy injectableScanStrategy;
    private final ConstructorFinder constructorFinder;

    public AnnotatedClassComponentSupplier(InjectableScanStrategy injectableScanStrategy, ConstructorFinder constructorFinder) {
        this.injectableScanStrategy = injectableScanStrategy;
        this.constructorFinder = constructorFinder;
    }

    @Override
    public Collection<Component> getComponents() {
        Collection<InjectableClass<?>> injectableClasses = injectableScanStrategy.scanInjectableClasses();
        return mapToComponents(injectableClasses);
    }

    private List<Component> mapToComponents(Collection<InjectableClass<?>> injectableClasses) {
        return injectableClasses
                .stream()
                .map(constructorFinder::findPrimaryConstructor)
                .map(AccessibilityHelper::setConstructorAccessible)
                .map(Component::fromConstructor)
                .toList();
    }
}
