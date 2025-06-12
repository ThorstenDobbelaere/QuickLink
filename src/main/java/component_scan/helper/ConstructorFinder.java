package component_scan.helper;

import component_scan.exceptions.ConstructorScanException;
import framework.setup.model.reflection.annotated_entities.InjectableClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ConstructorFinder {
    private final Predicate<Constructor<?>> primaryPredicate;

    public ConstructorFinder(Predicate<Constructor<?>> primaryPredicate) {
        this.primaryPredicate = primaryPredicate;
    }

    public <T> Constructor<T> findPrimaryConstructor(InjectableClass<T> componentClass) {
        return findPrimaryConstructor(componentClass.classType());
    }

    public <T> Constructor<T> findPrimaryConstructor(Class<T> type) {
        List<Constructor<T>> constructors = getConstructors(type);

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(type);

        var primaryConstructors = constructors.stream()
                .filter(this::isPrimary)
                .toList();

        if (primaryConstructors.size() == 1)
            return constructors.getFirst();

        if (primaryConstructors.size() > 1)
            throw ConstructorScanException.multiplePrimaryConstructors(type);

        if (constructors.size() > 1)
            throw ConstructorScanException.multipleConstructorsNoPrimary(type);

        return constructors.getFirst();
    }

    public <T> Constructor<T> findDefaultConstructor(InjectableClass<T> componentClass) {
        return findDefaultConstructor(componentClass.classType());
    }

    public <T> Constructor<T> findDefaultConstructor(Class<T> type) {
        List<Constructor<T>> constructors = getConstructors(type);

        if (constructors.isEmpty())
            throw ConstructorScanException.noConstructor(type);

        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == 0) {
                return uncheckedCast(constructor);
            }
        }

        throw ConstructorScanException.configNoDefaultConstructor(type);
    }

    private <T> List<Constructor<T>> getConstructors(Class<T> type) {
        List<Constructor<?>> constructors = Arrays.stream(type.getConstructors())
                .filter(this::isAccessible)
                .toList();

        return constructors.stream().map(this::<T>uncheckedCast).toList();
    }

    @SuppressWarnings("unchecked")
    private  <T> Constructor<T> uncheckedCast(Constructor<?> constructor) {
        return (Constructor<T>) constructor;
    }

    private boolean isAccessible(Constructor<?> constructor) {
        return !Modifier.isPrivate(constructor.getModifiers());
    }

    private boolean isPrimary(Constructor<?> constructor) {
        return primaryPredicate.test(constructor);
    }
}
