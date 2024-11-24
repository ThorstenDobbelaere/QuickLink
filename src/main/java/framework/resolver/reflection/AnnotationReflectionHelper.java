package framework.resolver.reflection;

import framework.context.QuickLinkContext;
import framework.exceptions.internal.InternalAnnotationException;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationReflectionHelper {

    public static Map<Class<?>, Class<? extends Annotation>> getTypesAnnotatedWithDirectSubtypes(QuickLinkContext context, Class<? extends Annotation> baseAnnotation) {
        Reflections projectReflections = context.getReflectionContext().getProjectReflections();
        var subAnnotations = getSubAnnotations(context, baseAnnotation, true);
        return subAnnotations.stream()
                .map(
                        a->projectReflections.getTypesAnnotatedWith(a)
                                .stream()
                                .collect(Collectors.<Class<?>, Class<?>, Class<? extends Annotation>>toMap(t->t, t->a))
                )
                .reduce(new HashMap<>(), (s1, s2)->{
                    s1.putAll(s2);
                    return s1;
                });
    }

    public static Set<Class<? extends Annotation>> getSubAnnotations(QuickLinkContext context, Class<? extends Annotation> baseAnnotation, boolean addBaseAnnotation) {
        Reflections annotationReflections = context.getReflectionContext().getAnnotationReflections();

        Set<Class<? extends Annotation>> subAnnotations = annotationReflections
                .getTypesAnnotatedWith(baseAnnotation)
                .stream()
                .filter(Class::isAnnotation)
                .map(AnnotationReflectionHelper::verifyAnnotation)
                .collect(Collectors.toSet());

        if(addBaseAnnotation)
            subAnnotations.add(baseAnnotation);

        return subAnnotations;
    }

    private static Class<? extends Annotation> verifyAnnotation(Class<?> annotation) {
        if(annotation == null){
            throw InternalAnnotationException.nullAnnotation();
        }
        if(annotation.isAnnotation()){
            return annotation.asSubclass(Annotation.class);
        }
        throw InternalAnnotationException.expectedAnnotation(annotation);
    }
}
