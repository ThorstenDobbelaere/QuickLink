package component_scan.strategies.contracts;

import framework.setup.model.reflection.annotated_entities.AnnotatedMethod;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;

import java.util.Collection;

public interface AnnotationReflectionStrategy {
    Collection<AnnotatedMethod> getMethodsAnnotatedWith(Class<?> type, AnnotationSet annotations);
    Collection<InjectableClass<?>> getClassesAnnotatedWith(AnnotationSet annotations);
}
