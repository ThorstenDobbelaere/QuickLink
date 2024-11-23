package provider;

import org.reflections.Reflections;
import reflection.ReflectionInstances;
import resolver.ComponentBuilder;
import resolver.InjectionEntries;

public class QuickLink {
    public static void run(Class<?> root){
        String packageName = root.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        ReflectionInstances.setProjectReflections(reflections);

        InjectionEntries.init();
        ComponentBuilder.init();

        var controllerMapping = ComponentBuilder.getInstance().getControllerMapping();
        controllerMapping.forEach(c-> System.out.println(c.getMapping() + " " + c.getController()));
    }
}
