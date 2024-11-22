package reflection;

import exceptions.internal.InternalAnnotationException;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeepReflectionHelper {
    public static Set<Class<?>> getTypesMetaAnnotatedWith(Class<? extends Annotation> baseAnnotation) {
        Reflections annotationReflections = ReflectionInstances.getAnnotationReflections();
        Reflections projectReflections = ReflectionInstances.getProjectReflections();


        Set<Class<? extends Annotation>> validAnnotations = annotationReflections
                .getTypesAnnotatedWith(baseAnnotation)
                .stream()
                .filter(Class::isAnnotation)
                .map(DeepReflectionHelper::verifyAnnotation)
                .collect(Collectors.toSet());

        validAnnotations.add(baseAnnotation);

        return validAnnotations
                .stream()
                .map(projectReflections::getTypesAnnotatedWith)
                .reduce(new LinkedHashSet<>(), (s1, s2)-> {
                    s1.addAll(s2);
                    return s1;
                });
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
