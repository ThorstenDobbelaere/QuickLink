package framework.setup.strategies;

import component_scan.annotations.Injectable;
import component_scan.annotations.clarification.PrimaryConstructor;
import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.injection.semantic.Repository;
import component_scan.annotations.injection.semantic.Service;
import component_scan.annotations.interception.Timed;
import component_scan.helper.ConstructorFinder;
import framework.context.config.ComponentScanScope;
import framework.context.config.QuickLinkStrategies;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;
import framework.setup.strategies.implementations.AnnotationBasedInjectableScanStrategy;
import framework.setup.strategies.implementations.InterceptMethodScanStrategyImpl;
import framework.setup.strategies.implementations.ReflectionsComponentScanStrategy;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class DefaultStrategies {
    private DefaultStrategies() {}
    private static final Map<ComponentScanScope, ComponentScanStrategy> cachedComponentScanStrategies = new HashMap<>();

    public static InjectableScanStrategy injectableScanStrategy(ComponentScanStrategy componentScanStrategy) {
        Collection<Class<? extends Annotation>> injectableAnnotations = Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        );

        return new AnnotationBasedInjectableScanStrategy(
                componentScanStrategy,
                AnnotationSet.of(injectableAnnotations)
        );
    }

    // Use caching to avoid recreating Reflections object and scanning the classpath multiple times.
    public static ComponentScanStrategy componentScanStrategy(ComponentScanScope scope) {
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

        ComponentScanStrategy strategy = new ReflectionsComponentScanStrategy(reflections);
        cachedComponentScanStrategies.put(scope, strategy);
        return strategy;
    }

    public static InterceptMethodScanStrategy interceptMethodScanStrategy(
            ComponentScanStrategy componentScanStrategy,
            InjectableScanStrategy injectableScanStrategy
    ) {
        Collection<Class<? extends Annotation>> interceptMethodAnnotations = Set.of(
                Timed.class
        );

        AnnotationSet annotationSet = AnnotationSet.of(interceptMethodAnnotations);
        return new InterceptMethodScanStrategyImpl(
                componentScanStrategy,
                injectableScanStrategy,
                annotationSet
        );
    }

    public static ConstructorFinder constructorFinder() {
        Predicate<Constructor<?>> isPrimary = c -> c.getAnnotation(PrimaryConstructor.class) != null;
        return new ConstructorFinder(isPrimary);
    }

    public static QuickLinkStrategies strategies(ComponentScanScope scope) {
        ComponentScanStrategy componentScanStrategy = componentScanStrategy(scope);
        InjectableScanStrategy injectableScanStrategy = injectableScanStrategy(componentScanStrategy);
        InterceptMethodScanStrategy interceptMethodScanStrategy = interceptMethodScanStrategy(
                componentScanStrategy,
                injectableScanStrategy
        );
        ConstructorFinder constructorFinder = constructorFinder();

        return new QuickLinkStrategies(
                componentScanStrategy,
                injectableScanStrategy,
                interceptMethodScanStrategy,
                constructorFinder
        );
    }
}
