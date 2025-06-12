package framework.setup.strategies.contracts;

import framework.setup.model.Component;

import java.util.Collection;

public interface ComponentSupplier {
    Collection<Component> getComponents();
}
