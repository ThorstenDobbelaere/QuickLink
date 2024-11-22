package resolver;

import annotations.injection.Injectable;
import annotations.injection.config.Bean;
import annotations.injection.config.Config;
import exceptions.internal.MapMethodObjectInternalError;
import model.ClassEntry;
import org.reflections.Reflections;
import reflection.DeepReflectionHelper;
import reflection.ReflectionInstances;
import resolver.helper.AccessibilityHelper;
import resolver.helper.constructor.ConfigConstructorHelper;
import resolver.helper.constructor.InjectableConstructorFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class InjectionEntries {
    private static InjectionEntries instance;
    private Set<ClassEntry> entrySet;

    public Set<ClassEntry> getEntrySet() {
        return entrySet;
    }

    public static InjectionEntries getInstance() {
        if (instance == null) {
            throw new NullPointerException("Component mappings accessed before initialization");
        }
        return instance;
    }

    public static void init() {
        instance = new InjectionEntries();
        Reflections reflections = ReflectionInstances.getProjectReflections();

        Set<Class<?>> injectables = DeepReflectionHelper.getTypesMetaAnnotatedWith(Injectable.class);
        List<Constructor<?>> injectableConstructors = injectables.stream()
                .map(InjectableConstructorFinder::tryGetConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .collect(Collectors.toUnmodifiableList());

        Set<Class<?>> configs = reflections.getTypesAnnotatedWith(Config.class);
        List<Constructor<?>> configDefaultConstructors = configs.stream()
                .map(ConfigConstructorHelper::tryFindDefaultConstructor)
                .map(AccessibilityHelper::trySetConstructorAccessible)
                .collect(Collectors.toUnmodifiableList());

        Map<Class<?>, Object> configObjects = configDefaultConstructors
                .stream()
                .map(ClassEntry::new)
                .peek(ClassEntry::create)
                .collect(Collectors.toUnmodifiableMap(ClassEntry::getClass, ClassEntry::getInstance));

        List<Method> beanMethods = configs.stream()
                .map((config) ->
                        Arrays.stream(config.getMethods())
                                .filter(method -> method.isAnnotationPresent(Bean.class))
                                .map(AccessibilityHelper::trySetMethodAccessible)
                                .toList())
                .reduce(new LinkedList<>(), (l1, l2)->{
                    l1.addAll(l2);
                    return l1;
                });

        List<ClassEntry> beanEntries = beanMethods.stream()
                .map(m->tryMapToConfigInstance(configObjects, m))
                .toList();

        List<ClassEntry> injectableEntries = injectableConstructors.stream()
                .map(ClassEntry::new)
                .toList();

        List<ClassEntry> entries = new ArrayList<>();
        entries.addAll(beanEntries);
        entries.addAll(injectableEntries);

        instance.entrySet = new LinkedHashSet<>(entries);
    }



    private static ClassEntry tryMapToConfigInstance(Map<Class<?>, Object> configObjects, Method method) {
        Class<?> methodClass = method.getDeclaringClass();
        if(!configObjects.containsKey(methodClass)) {
            throw MapMethodObjectInternalError.configNotFound(methodClass);
        }
        Object value = configObjects.get(methodClass);
        return new ClassEntry(method, value);
    }
}
