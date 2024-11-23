package exceptions;

import java.lang.reflect.Method;

public class DuplicateException extends RuntimeException {
    private DuplicateException(String message) {
        super(message);
    }

    public static DuplicateException duplicateComponent(Class<?> componentClass) {
        return new DuplicateException("Duplicate component: " + componentClass.getName());
    }

    public static DuplicateException duplicateMapping(String mapping, Method method1, Method method2) {
        return new DuplicateException(String.format("Duplicate method mapping: %s -> %s or %s", mapping, method1.getName(), method2.getName()));
    }
}
