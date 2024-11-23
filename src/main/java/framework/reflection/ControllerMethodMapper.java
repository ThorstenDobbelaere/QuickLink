package framework.reflection;

import framework.annotations.mapping.Mapping;
import framework.exceptions.DuplicateException;
import framework.model.MappedController;
import framework.model.MappedMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ControllerMethodMapper {
    public static List<MappedMethod> map(Set<MappedController> controllers) {
        return controllers.stream()
                .map(ControllerMethodMapper::mapMethodsForController)
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private static List<MappedMethod> mapMethodsForController(MappedController controller) {
        var mappings = AnnotationReflectionHelper.getSubAnnotations(Mapping.class, false);
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
                .map(entry-> new MappedMethod(controller.controller(), entry.getKey(), entry.getValue(), controller.mapping()))
                .toList();
    }
}
