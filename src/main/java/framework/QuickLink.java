package framework;

import framework.requesthandlers.RequestHandler;
import framework.resolver.MethodMappings;
import org.reflections.Reflections;
import framework.reflection.ReflectionInstances;
import framework.resolver.ComponentBuilder;
import framework.resolver.InjectionEntries;

public class QuickLink {
    public static void run(Class<?> root){
        String packageName = root.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        ReflectionInstances.setProjectReflections(reflections);

        InjectionEntries.init();
        ComponentBuilder.init();

        var controllerMapping = ComponentBuilder.getInstance().getControllerMapping();
        MethodMappings.init(controllerMapping);

        var methodMappings = MethodMappings.getHandlers();
        for(RequestHandler<?> handler : methodMappings){
            System.out.println(handler.getMapping());
            handler.handle("");
        }
    }

    private QuickLink(){}
}
