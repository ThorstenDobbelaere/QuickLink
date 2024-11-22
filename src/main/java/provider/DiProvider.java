package provider;

import org.reflections.Reflections;
import reflection.ReflectionInstances;
import resolver.InjectionEntries;

public class DiProvider {
    public static void run(Class<?> root){
        String packageName = root.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        ReflectionInstances.setProjectReflections(reflections);


        InjectionEntries.init();
        InjectionEntries.getInstance().getEntrySet().forEach(entry -> System.out.println(entry.getType()));
    }
}
