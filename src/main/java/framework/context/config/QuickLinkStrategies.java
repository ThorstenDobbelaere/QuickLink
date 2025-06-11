package framework.context.config;

import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;

public record QuickLinkStrategies(
        ComponentScanStrategy componentScanStrategy,
        InjectableScanStrategy injectableScanStrategy,
        InterceptMethodScanStrategy interceptMethodScanStrategy
) {
}
