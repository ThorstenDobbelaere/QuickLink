package framework.setup.model.reflection.annotated_entities;

import framework.setup.model.reflection.annotation.AnnotationType;

import java.util.Collection;

public record InjectableClassWithInterceptedMethods<T>(
        Class<T> classType,
        AnnotationType annotationType,
        Collection<AnnotatedMethod> annotatedMethods
) {
}
