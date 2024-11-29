package framework.setup.helper.constructor;

import framework.annotations.clarification.PrimaryConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class BasicConstructorHelper {
    public static boolean isAccessible(Constructor<?> constructor) {
        return !Modifier.isPrivate(constructor.getModifiers());
    }

    public static boolean isPrimary(Constructor<?> constructor) {
        return constructor.getAnnotation(PrimaryConstructor.class) != null;
    }
}
