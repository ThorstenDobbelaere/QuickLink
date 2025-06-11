package component_scan.helper;

import component_scan.annotations.clarification.PrimaryConstructor;
import component_scan.exceptions.ConstructorScanException;
import framework.setup.model.reflection.annotated_entities.InjectableClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ConstructorFinder {
    private ConstructorFinder() {}

    public static <T> Constructor<T> findPrimaryConstructor(InjectableClass<T> componentClass) {
        Class<T> classType = componentClass.classType();
        List<Constructor<T>> constructors = getConstructors(componentClass);

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(classType);

        var primaryConstructors = constructors.stream()
                .filter(ConstructorFinder::isPrimary)
                .toList();

        if (primaryConstructors.size() == 1)
            return constructors.getFirst();

        if (primaryConstructors.size() > 1)
            throw ConstructorScanException.multiplePrimaryConstructors(classType);

        if (constructors.size() > 1)
            throw ConstructorScanException.multipleConstructorsNoPrimary(classType);

        return constructors.getFirst();
    }

    public static <T> Constructor<T> findDefaultConstructor(InjectableClass<T> componentClass) {
        Class<?> type = componentClass.classType();
        List<Constructor<T>> constructors = getConstructors(componentClass);

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(type);

        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == 0) {
                return uncheckedCast(constructor);
            }
        }

        throw ConstructorScanException.configNoDefaultConstructor(type);
    }

    private static <T> List<Constructor<T>> getConstructors(InjectableClass<T> componentClass) {
        Class<?> type = componentClass.classType();
        List<Constructor<?>> constructors = Arrays.stream(type.getConstructors())
                .filter(ConstructorFinder::isAccessible)
                .toList();

        return constructors.stream().map(ConstructorFinder::<T>uncheckedCast).toList();
    }

    @SuppressWarnings("unchecked")
    private static  <T> Constructor<T> uncheckedCast(Constructor<?> constructor) {
        return (Constructor<T>) constructor;
    }

    private static boolean isAccessible(Constructor<?> constructor) {
        return !Modifier.isPrivate(constructor.getModifiers());
    }

    private static boolean isPrimary(Constructor<?> constructor) {
        return constructor.getAnnotation(PrimaryConstructor.class) != null;
    }
}
