package exceptions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AccessException extends RuntimeException {
    private AccessException(String message) {
        super(message);
    }

    public static AccessException ofConstructor(Constructor<?> constructor, Exception cause) {
        return new AccessException(String.format("Unable to set constructor for class %s to accessible: %s", constructor.getDeclaringClass().getName(), cause.getMessage()));
    }

    public static AccessException ofMethod(Method method, Exception cause) {
        return new AccessException(String.format("Unable to set method %s for class %s to accessible: %s", method.getName(), method.getDeclaringClass().getName(), cause.getMessage()));
    }
}
