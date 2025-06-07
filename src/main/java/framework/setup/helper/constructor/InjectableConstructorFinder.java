package framework.setup.helper.constructor;

import framework.exceptions.componentscan.ConstructorScanException;
import framework.setup.helper.AccessibilityHelper;
import framework.setup.model.reflection.annotated_class.InjectableClass;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class InjectableConstructorFinder {
    private InjectableConstructorFinder() {}

    public static <T> Constructor<T> tryGetConstructor(InjectableClass<T> componentClass) {
        Class<T> classType = componentClass.classType();

        var constructors = Arrays.stream(classType.getDeclaredConstructors())
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .filter(BasicConstructorHelper::isAccessible)
                .toList();

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(classType);


        var primaryConstructors = constructors.stream()
                .filter(BasicConstructorHelper::isPrimary)
                .toList();

        if (primaryConstructors.size() == 1)
            return uncheckedCast(constructors.getFirst());

        if (primaryConstructors.size() > 1)
            throw ConstructorScanException.multiplePrimaryConstructors(classType);

        if (constructors.size() > 1)
            throw ConstructorScanException.multipleConstructorsNoPrimary(classType);

        return uncheckedCast(constructors.getFirst());
    }

    @SuppressWarnings("unchecked")
    private static  <T> Constructor<T> uncheckedCast(Constructor<?> constructor) {
        return (Constructor<T>) constructor;
    }
}
