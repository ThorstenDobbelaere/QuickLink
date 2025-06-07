package framework.setup.model.reflection.annotated_class;

import framework.setup.model.reflection.annotation.AnnotationType;

import java.lang.reflect.Method;
import java.util.Set;

public record InjectableClassWithTimedMethods<T>(
        Class<T> classType,
        AnnotationType annotationType,
        Set<Method> timedMethods
) {
}
