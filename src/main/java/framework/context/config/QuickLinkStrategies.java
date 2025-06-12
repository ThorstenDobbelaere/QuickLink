package framework.context.config;

import component_scan.strategies.contracts.ComponentSupplier;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;

public record QuickLinkStrategies(
        InterceptMethodScanStrategy interceptMethodScanStrategy,
        ComponentSupplier componentSupplier
) {
}
