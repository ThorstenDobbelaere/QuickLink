package framework.resolver.reflection;

import framework.annotations.mapping.Mapping;
import framework.context.QuickLinkContext;
import framework.exceptions.scanning.DuplicateException;
import framework.resolver.model.MappedController;
import framework.resolver.model.MappedControllerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ControllerMethodMapper {
    public static List<MappedControllerMethod> map(QuickLinkContext context, Set<MappedController> controllers) {
        return controllers.stream()
                .map(mappedController -> mapMethodsForController(context, mappedController))
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private static List<MappedControllerMethod> mapMethodsForController(QuickLinkContext context, MappedController controller) {
        var mappings = AnnotationReflectionHelper.getSubAnnotations(context, Mapping.class, false);
        var methods = controller.controller().getClass().getDeclaredMethods();

        Map<Method, Annotation> annotatedMethodsMap = new HashMap<>();
        for(Method method : methods){
            for(Class<? extends Annotation> annotation : mappings){
                if(method.isAnnotationPresent(annotation)){
                    if(annotatedMethodsMap.containsKey(method)){
                        throw DuplicateException.duplicateAnnotation(method, annotatedMethodsMap.get(method).getClass(), annotation);
                    }
                    annotatedMethodsMap.put(method, method.getAnnotation(annotation));
                }
            }
        }

        return annotatedMethodsMap.entrySet().stream()
                .map(entry-> new MappedControllerMethod(controller.controller(), entry.getKey(), entry.getValue(), controller.mapping()))
                .toList();
    }
}
