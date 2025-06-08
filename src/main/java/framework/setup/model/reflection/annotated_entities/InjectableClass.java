package framework.setup.model.reflection.annotated_entities;

import framework.setup.model.reflection.annotation.AnnotationType;

public record InjectableClass<T>(Class<T> classType, AnnotationType annotationType) {
}
