package framework.setup.model.reflection.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record AnnotationSet(Set<AnnotationType> annotations) {

    public static AnnotationSet of(Collection<Class<? extends Annotation>> annotations) {
        Set<AnnotationType> set = annotations.stream()
                .map(AnnotationType::new)
                .collect(Collectors.toSet());

        return new AnnotationSet(set);
    }

    public static AnnotationSet of(Class<? extends Annotation> annotation) {
        Set<AnnotationType> set = Set.of(new AnnotationType(annotation));
        return new AnnotationSet(set);
    }
}
