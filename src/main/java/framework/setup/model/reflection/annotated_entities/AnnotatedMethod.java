package framework.setup.model.reflection.annotated_entities;

import framework.setup.model.reflection.annotation.AnnotationType;

import java.lang.reflect.Method;

public record AnnotatedMethod(Method method, AnnotationType annotationType) {
}
