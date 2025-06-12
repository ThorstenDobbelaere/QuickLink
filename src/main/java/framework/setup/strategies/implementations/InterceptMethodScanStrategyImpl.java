package framework.setup.strategies.implementations;

import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;
import framework.setup.model.reflection.annotation.AnnotationSet;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;

import java.util.Collection;

public class InterceptMethodScanStrategyImpl implements InterceptMethodScanStrategy {
    private final AnnotationReflectionStrategy annotationReflectionStrategy;
    private final InjectableScanStrategy injectableScanStrategy;
    private final AnnotationSet annotationsToScan;

    public InterceptMethodScanStrategyImpl(AnnotationReflectionStrategy annotationReflectionStrategy, InjectableScanStrategy injectableScanStrategy, AnnotationSet annotationsToScan) {
        this.annotationReflectionStrategy = annotationReflectionStrategy;
        this.injectableScanStrategy = injectableScanStrategy;
        this.annotationsToScan = annotationsToScan;
    }

    @Override
    public Collection<InjectableClassWithInterceptedMethods<?>> getInterceptedMethods() {
        Collection<InjectableClass<?>> injectableClasses = injectableScanStrategy.scanInjectableClasses();
        return scanInterceptedMethods(injectableClasses);
    }

    private Collection<InjectableClassWithInterceptedMethods<?>> scanInterceptedMethods(Collection<InjectableClass<?>> classList) {
        return classList
                .stream()
                .<InjectableClassWithInterceptedMethods<?>> map(this::scanInterceptedMethods)
                .filter(injectable -> !injectable.annotatedMethods().isEmpty())
                .toList();
    }

    private <T> InjectableClassWithInterceptedMethods<T> scanInterceptedMethods(InjectableClass<T> injectableClass) {
        Class<T> type = injectableClass.classType();
        var methods = this.annotationReflectionStrategy.getMethodsAnnotatedWith(type, annotationsToScan);
        return new InjectableClassWithInterceptedMethods<>(type, injectableClass.annotationType(), methods);
    }


}
