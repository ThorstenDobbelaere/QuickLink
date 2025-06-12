package reflection;

import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import framework.context.config.ComponentScanScope;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultReflectionFactory {
    private static final Map<ComponentScanScope, AnnotationReflectionStrategy> cachedComponentScanStrategies = new HashMap<>();

    private DefaultReflectionFactory() {}

    // Use caching to avoid recreating Reflections object and scanning the classpath multiple times.
    public static AnnotationReflectionStrategy componentScanStrategy(ComponentScanScope scope) {
        if (cachedComponentScanStrategies.containsKey(scope)) {
            return cachedComponentScanStrategies.get(scope);
        }
        Collection<Class<?>> rootClasses = scope.getPackageRootClasses();

        FilterBuilder filter = new FilterBuilder();

        rootClasses.stream()
                .map(root -> root.getPackage().getName())
                .distinct()
                .forEach(filter::includePackage);

        List<URL> urls = rootClasses.stream()
                .map(root -> ClasspathHelper.forPackage(root.getPackage().getName()))
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setUrls(urls)
                .setInputsFilter(filter);

        Reflections reflections = new Reflections(builder);

        AnnotationReflectionStrategy strategy = new ReflectionsAnnotationReflectionStrategy(reflections);
        cachedComponentScanStrategies.put(scope, strategy);
        return strategy;
    }
}
