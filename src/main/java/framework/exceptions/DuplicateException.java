package framework.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DuplicateException extends RuntimeException {
    private DuplicateException(String message) {
        super(message);
    }

    public static DuplicateException duplicateComponent(Class<?> componentClass) {
        return new DuplicateException("Duplicate component: " + componentClass.getName());
    }

    public static DuplicateException mappingAmbiguity(String mapping, Method method1, Method method2) {
        return new DuplicateException(String.format("Duplicate method mapping: %s -> %s or %s", mapping, method1.getName(), method2.getName()));
    }

    public static DuplicateException methodAmbiguity(Method method) {
        return new DuplicateException(String.format("Method %s has multiple mappings", method.getName()));
    }

    public static DuplicateException duplicateAnnotation(Method method, Class<? extends Annotation> annotation1, Class<? extends Annotation> annotation2) {
        return new DuplicateException(String.format("Duplicate annotation for method %s: %s and %s", method.getName(), annotation1.getName(), annotation2.getName()));
    }
}
