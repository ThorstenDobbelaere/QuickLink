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
        controllerMapping.forEach((k, v)->{
            System.out.println(k+ ": " + v.getClass().getName());
        });
        //InjectionEntries.getInstance().getEntrySet().forEach(entry -> System.out.println(entry.getType()));
    }
}
