package framework.exceptions.scanning;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DuplicateException extends RuntimeException {
    private DuplicateException(String message) {
        super(message);
    }

    public static DuplicateException duplicateComponent(Class<?> componentClass) {
        return new DuplicateException("Duplicate component: " + componentClass.getName());
    }

    public static DuplicateException duplicateAnnotation(Method method, Class<? extends Annotation> annotation1, Class<? extends Annotation> annotation2) {
        return new DuplicateException(String.format("Duplicate annotation for method %s: %s and %s", method.getName(), annotation1.getName(), annotation2.getName()));
    }
}
