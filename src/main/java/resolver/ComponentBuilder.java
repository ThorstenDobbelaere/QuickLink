package resolver;

import exceptions.internal.InternalException;
import model.ClassEntry;
import model.MappedController;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentBuilder {
    private Set<MappedController> controllerMapping = null;
    private static ComponentBuilder instance = new ComponentBuilder();

    public Set<MappedController> getControllerMapping() {
        return controllerMapping;
    }

    public static ComponentBuilder getInstance() {
        if(instance == null) {
            throw new InternalException("Component builder not initialized");
        }
        return instance;
    }

    public static void init(){
        Set<ClassEntry> entries = InjectionEntries.getInstance().getEntrySet();
        Map<Class<?>, ClassEntry> entryMap = entries.stream()
                .collect(Collectors.toUnmodifiableMap(ClassEntry::getType, entry -> entry));

        Map<ClassEntry, Object> implementationMapping = entries.stream()
                .collect(Collectors.toUnmodifiableMap(
                        classEntry -> classEntry,
                        classEntry -> resolve(classEntry.getType(), entryMap)
                ));

        Set<MappedController> controllerMapping = implementationMapping.entrySet().stream()
                .filter(entry->entry.getKey().isController())
                .map(entry->new MappedController(entry.getKey().getControllerPath(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());

        instance = new ComponentBuilder();
        instance.controllerMapping = controllerMapping;
    }

    private static Object resolve(Class<?> type, Map<Class<?>, ClassEntry> entryMap) {
        if(!entryMap.containsKey(type)){
            throw new InternalException("Class " + type + " not found");
        }
        ClassEntry entry = entryMap.get(type);
        if(entry.isCached())
            return entry.getInstance();

        Class<?>[] dependencies = entry.getDependencies();
        Object[] dependencyObjects = Arrays.stream(dependencies)
                .map(
                    t->resolve(t, entryMap)
                ).toList().toArray();
        entry.create(dependencyObjects);
        return entry.getInstance();
    }
}
