package resolver.helper.constructor;

import exceptions.ConstructorScanException;
import resolver.helper.AccessibilityHelper;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class InjectableConstructorFinder {
    public static Constructor<?> tryGetConstructor(Class<?> componentClass) {

        List<Constructor<?>> constructors = Arrays.stream(componentClass.getDeclaredConstructors())
                .<Constructor<?>>map(AccessibilityHelper::trySetConstructorAccessible)
                .filter(BasicConstructorHelper::isAccessible)
                .toList();

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(componentClass);


        List<Constructor<?>> primaryConstructors = constructors.stream()
                .filter(BasicConstructorHelper::isPrimary)
                .toList();

        if (primaryConstructors.size() == 1)
            return constructors.getFirst();

        if (primaryConstructors.size() > 1)
            throw ConstructorScanException.multiplePrimaryConstructors(componentClass);

        if (constructors.size() > 1)
            throw ConstructorScanException.multipleConstructorsNoPrimary(componentClass);

        return constructors.getFirst();
    }
}
