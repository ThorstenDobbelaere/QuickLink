package framework.setup.strategies;

import framework.annotations.Injectable;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.injection.semantic.Repository;
import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;
import framework.setup.strategies.implementations.AnnotationBasedInjectableScanStrategy;
import framework.setup.strategies.implementations.InterceptMethodScanStrategyImpl;
import framework.setup.strategies.implementations.ReflectionsComponentScanStrategy;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

public class DefaultStrategies {
    private DefaultStrategies() {}
    private static ComponentScanStrategy cachedComponentScanStrategy = null;
    private static InjectableScanStrategy cachedInjectableScanStrategy = null;
    private static InterceptMethodScanStrategy cachedInterceptMethodScanStrategy = null;

    public static InjectableScanStrategy injectableScanStrategy() {
        if (cachedInjectableScanStrategy != null) {
            return cachedInjectableScanStrategy;
        }

        ComponentScanStrategy strategy = componentScanStrategy();
        Collection<Class<? extends Annotation>> injectableAnnotations = Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        );

        cachedInjectableScanStrategy = new AnnotationBasedInjectableScanStrategy(
                strategy,
                new AnnotationSet(injectableAnnotations)
        );

        return cachedInjectableScanStrategy;
    }

    public static ComponentScanStrategy componentScanStrategy(String packageName) {
        Reflections reflections = new Reflections(packageName);
        cachedComponentScanStrategy = new ReflectionsComponentScanStrategy(reflections);
        return cachedComponentScanStrategy;
    }

    public static InterceptMethodScanStrategy interceptMethodScanStrategy() {
        if (cachedInterceptMethodScanStrategy != null) {
            return cachedInterceptMethodScanStrategy;
        }

        Collection<Class<? extends Annotation>> interceptMethodAnnotations = Set.of(
                Timed.class
        );

        AnnotationSet annotationSet = new AnnotationSet(interceptMethodAnnotations);
        ComponentScanStrategy componentScanStrategy = componentScanStrategy();
        cachedInterceptMethodScanStrategy = new InterceptMethodScanStrategyImpl(componentScanStrategy, annotationSet);
        return cachedInterceptMethodScanStrategy;
    }

    private static ComponentScanStrategy componentScanStrategy() {
        if (cachedComponentScanStrategy != null) {
            return cachedComponentScanStrategy;
        }
        throw new RuntimeException("Component scan strategy not initialized");
    }
}
