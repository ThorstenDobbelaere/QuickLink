package framework.resolver.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public record MappedMethod(Object controller, Method method, Annotation annotation, String controllerMapping) {
}
