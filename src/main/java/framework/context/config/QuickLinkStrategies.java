package framework.context.config;

import component_scan.helper.ConstructorFinder;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;

public record QuickLinkStrategies(
        AnnotationReflectionStrategy annotationReflectionStrategy,
        InjectableScanStrategy injectableScanStrategy,
        InterceptMethodScanStrategy interceptMethodScanStrategy,
        ConstructorFinder constructorFinder
) {
}
