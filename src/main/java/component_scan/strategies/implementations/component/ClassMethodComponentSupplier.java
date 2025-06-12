package component_scan.strategies.implementations.component;

import helper.ConstructorFinder;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.exceptions.internal.ObjectCreationException;
import framework.setup.model.Component;
import component_scan.strategies.contracts.ComponentSupplier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

public class ClassMethodComponentSupplier<T> implements ComponentSupplier {
    private final Class<T> componentClass;
    private final ConstructorFinder constructorFinder;

    public ClassMethodComponentSupplier(
            Class<T> componentClass,
            ConstructorFinder constructorFinder
    ) {
        this.componentClass = componentClass;
        this.constructorFinder = constructorFinder;
    }

    @Override
    public Collection<Component> getComponents() {
        T objectInstance = instantiateObject();

        return Arrays.stream(DefaultConfigurationMappings.class
                        .getDeclaredMethods())
                .map(m->new Component(m, objectInstance))
                .toList();
    }

    private T instantiateObject() {
        Constructor<T> constructor = constructorFinder.findDefaultConstructor(componentClass);
        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw ObjectCreationException.invokingDefaultConstructor(e);
        }

    }
}
