package framework.setup.model.reflection.annotation;

import java.lang.annotation.Annotation;

public record AnnotationType(Class<? extends Annotation> annotation) {
}
