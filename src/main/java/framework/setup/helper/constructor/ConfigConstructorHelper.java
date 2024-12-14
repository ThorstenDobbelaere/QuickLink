package framework.setup.helper.constructor;

import framework.exceptions.componentscan.ConstructorScanException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ConfigConstructorHelper {
    public static Constructor<?> tryFindDefaultConstructor(Class<?> componentClass) {
        List<Constructor<?>> constructors = Arrays.stream(componentClass.getConstructors())
                .filter(BasicConstructorHelper::isAccessible)
                .toList();
        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(componentClass);

        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == 0) {
                return constructor;
            }
        }

        throw ConstructorScanException.configNoDefaultConstructor(componentClass);
    }
}
