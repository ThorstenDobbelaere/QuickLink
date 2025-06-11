package framework.setup.helper.constructor;

import framework.exceptions.componentscan.ConstructorScanException;
import framework.setup.model.reflection.annotated_entities.InjectableClass;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ConfigConstructorHelper {
    private ConfigConstructorHelper() {}

    public static Constructor<?> tryFindDefaultConstructor(InjectableClass<?> componentClass) {
        Class<?> type = componentClass.classType();
        List<Constructor<?>> constructors = Arrays.stream(type.getConstructors())
                .filter(BasicConstructorHelper::isAccessible)
                .toList();
        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(type);

        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == 0) {
                return constructor;
            }
        }

        throw ConstructorScanException.configNoDefaultConstructor(type);
    }
}
