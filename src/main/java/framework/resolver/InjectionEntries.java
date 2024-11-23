package framework.resolver;

import framework.annotations.Injectable;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import framework.exceptions.DuplicateException;
import framework.exceptions.internal.MapMethodObjectInternalError;
import framework.model.ClassEntry;
import org.reflections.Reflections;
import framework.reflection.AnnotationReflectionHelper;
import framework.reflection.ReflectionInstances;
import framework.resolver.helper.AccessibilityHelper;
import framework.resolver.helper.constructor.ConfigConstructorHelper;
import framework.resolver.helper.constructor.InjectableConstructorFinder;

import java.lang.annotation.Annotation;
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

        Map<Class<?>, Class<? extends Annotation>> injectables = AnnotationReflectionHelper.getTypesAnnotatedWithDirectSubtypes(Injectable.class);
        List<Constructor<?>> injectableConstructors = injectables
                .keySet()
                .stream()
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
                .collect(Collectors.toUnmodifiableMap(ClassEntry::getType, ClassEntry::getInstance));

        List<Method> beanMethods = configs.stream()
                .map((config) ->
                        Arrays.stream(config.getDeclaredMethods())
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

        List<ClassEntry> duplicates = entries.stream()
                .filter(entry1->entries.stream()
                        .filter(entry2-> entry1.getType().equals(entry2.getType())
                                ).count()>1)
                .distinct()
                .toList();

        if (!duplicates.isEmpty()) {
            throw DuplicateException.duplicateComponent(duplicates.getFirst().getType());
        }

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
