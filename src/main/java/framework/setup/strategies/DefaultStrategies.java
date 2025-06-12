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
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;
import component_scan.strategies.implementations.AnnotationBasedInjectableScanStrategy;
import framework.setup.strategies.implementations.InterceptMethodScanStrategyImpl;
import component_scan.strategies.implementations.ReflectionsAnnotationReflectionStrategy;
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
    private static final Map<ComponentScanScope, AnnotationReflectionStrategy> cachedComponentScanStrategies = new HashMap<>();

    public static InjectableScanStrategy injectableScanStrategy(AnnotationReflectionStrategy annotationReflectionStrategy) {
        Collection<Class<? extends Annotation>> injectableAnnotations = Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        );

        return new AnnotationBasedInjectableScanStrategy(
                annotationReflectionStrategy,
                AnnotationSet.of(injectableAnnotations)
        );
    }

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

    public static InterceptMethodScanStrategy interceptMethodScanStrategy(
            AnnotationReflectionStrategy annotationReflectionStrategy,
            InjectableScanStrategy injectableScanStrategy
    ) {
        Collection<Class<? extends Annotation>> interceptMethodAnnotations = Set.of(
                Timed.class
        );

        AnnotationSet annotationSet = AnnotationSet.of(interceptMethodAnnotations);
        return new InterceptMethodScanStrategyImpl(
                annotationReflectionStrategy,
                injectableScanStrategy,
                annotationSet
        );
    }

    public static ConstructorFinder constructorFinder() {
        Predicate<Constructor<?>> isPrimary = c -> c.getAnnotation(PrimaryConstructor.class) != null;
        return new ConstructorFinder(isPrimary);
    }

    public static QuickLinkStrategies strategies(ComponentScanScope scope) {
        AnnotationReflectionStrategy annotationReflectionStrategy = componentScanStrategy(scope);
        InjectableScanStrategy injectableScanStrategy = injectableScanStrategy(annotationReflectionStrategy);
        InterceptMethodScanStrategy interceptMethodScanStrategy = interceptMethodScanStrategy(
                annotationReflectionStrategy,
                injectableScanStrategy
        );
        ConstructorFinder constructorFinder = constructorFinder();

        return new QuickLinkStrategies(
                annotationReflectionStrategy,
                injectableScanStrategy,
                interceptMethodScanStrategy,
                constructorFinder
        );
    }
}
