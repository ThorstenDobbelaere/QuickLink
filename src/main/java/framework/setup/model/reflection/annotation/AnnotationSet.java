package framework.setup.model.reflection.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record AnnotationSet(Set<AnnotationType> annotations) {

    public AnnotationSet(Collection<Class<? extends Annotation>> annotations) {
        this(annotations.stream()
                .map(AnnotationType::new)
                .collect(Collectors.toSet())
        );
    }
}
