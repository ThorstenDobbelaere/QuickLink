package framework.exceptions.componentscan;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AccessException extends RuntimeException {
    private AccessException(String message) {
        super(message);
    }

    public static AccessException ofConstructor(Constructor<?> constructor, Exception cause) {
        return new AccessException(String.format("Unable to set constructor for class %s to accessible: %s", constructor.getDeclaringClass().getName(), cause.getMessage()));
    }

    public static AccessException timedMethodPrivate(Method method, Exception cause) {
        return new AccessException(String.format("Unable to set method %s for class %s to accessible: %s", method.getName(), method.getDeclaringClass().getName(), cause.getMessage()));
    }

    public static AccessException timedMethodPrivate(Method method) {
        return new AccessException(String.format("Timed method %s in class %s is private", method.getName(), method.getDeclaringClass().getName()));
    }
}
