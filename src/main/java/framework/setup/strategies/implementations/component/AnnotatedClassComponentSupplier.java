package framework.setup.strategies.implementations.component;

import component_scan.helper.AccessibilityHelper;
import component_scan.helper.ConstructorFinder;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.strategies.contracts.ComponentSupplier;
import framework.setup.strategies.contracts.InjectableScanStrategy;

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
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .map(Component::fromConstructor)
                .toList();
    }
}
