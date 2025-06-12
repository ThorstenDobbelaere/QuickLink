package component_scan.strategies.contracts;

import framework.setup.model.reflection.annotated_entities.InjectableClass;

import java.util.Collection;

public interface InjectableScanStrategy {
    Collection<InjectableClass<?>> scanInjectableClasses();
}
