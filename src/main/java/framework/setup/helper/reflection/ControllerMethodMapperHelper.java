package framework.setup.helper.reflection;

import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;
import framework.exceptions.componentscan.DuplicateException;
import framework.setup.model.MappedController;
import framework.setup.model.MappedControllerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ControllerMethodMapperHelper {
    private static final Set<Class<? extends Annotation>> MAPPING_ANNOTATIONS = Set.of(
            InputMapping.class,
            OutputMapping.class,
            IOMapping.class
    );

    private ControllerMethodMapperHelper() {}

    public static List<MappedControllerMethod> getMappedMethodsForControllers(Set<MappedController> controllers) {
        return controllers.stream()
                .map(ControllerMethodMapperHelper::mapMethodsForController)
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });
    }

    private static List<MappedControllerMethod> mapMethodsForController(MappedController controller) {
        var methods = controller.controller().getClass().getDeclaredMethods();

        Map<Method, Annotation> annotatedMethodsMap = new HashMap<>();
        for(Method method : methods){
            for(Class<? extends Annotation> annotation : MAPPING_ANNOTATIONS){
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
