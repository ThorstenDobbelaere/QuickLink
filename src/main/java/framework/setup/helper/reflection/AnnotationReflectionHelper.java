package framework.setup.helper.reflection;

import framework.context.QuickLinkContext;
import framework.setup.model.reflection.annotated_class.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.model.reflection.annotation.AnnotationType;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationReflectionHelper {

    private AnnotationReflectionHelper() {}

    public static Set<InjectableClass<?>> getTypesAnnotatedWith(QuickLinkContext context, AnnotationSet annotationSet) {
        Reflections projectReflections = context.getReflectionContext().getProjectReflections();
        return annotationSet.annotations().stream()
                .map(AnnotationType::annotation)
                .map(a-> toInjectableClassSet(a, projectReflections))
                .reduce(new HashSet<>(), (s1, s2)->{
                    s1.addAll(s2);
                    return s1;
                });
    }

    private static Set<InjectableClass<?>> toInjectableClassSet(Class<? extends Annotation> annotation, Reflections reflections) {
        return reflections.getTypesAnnotatedWith(annotation)
                .stream()
                .map(type ->  (InjectableClass<?>) new InjectableClass<>(type, new AnnotationType(annotation)))
                .collect(Collectors.toSet());
    }
}
