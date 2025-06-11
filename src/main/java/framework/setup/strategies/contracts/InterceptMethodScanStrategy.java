package framework.setup.strategies.contracts;

import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;

import java.util.Collection;

public interface InterceptMethodScanStrategy {
    Collection<InjectableClassWithInterceptedMethods<?>> getInterceptedMethods();
}
