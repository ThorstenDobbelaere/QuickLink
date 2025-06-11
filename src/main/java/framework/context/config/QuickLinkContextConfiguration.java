package framework.context.config;

import framework.setup.strategies.DefaultStrategies;

public record QuickLinkContextConfiguration(
        LogFormatter logFormatter,
        ListenerConfiguration listenerConfiguration,
        RunMode runMode,
        QuickLinkStrategies strategies
) {

    public static class Builder {
        private RunMode runMode = RunMode.HTTP;
        private LogFormatter logFormatter = new LogFormatter();
        private ListenerConfiguration listenerConfiguration = new ListenerConfiguration(8080, "/stop");
        private QuickLinkStrategies strategies = null;
        private Class<?> rootClass = null;

        public Builder setRunMode(RunMode runMode) {
            this.runMode = runMode;
            return this;
        }

        public Builder setStrategies(QuickLinkStrategies strategies) {
            this.strategies = strategies;
            return this;
        }

        public Builder setLogFormatter(LogFormatter logFormatter) {
            this.logFormatter = logFormatter;
            return this;
        }

        public Builder setListenerConfiguration(ListenerConfiguration listenerConfiguration) {
            this.listenerConfiguration = listenerConfiguration;
            return this;
        }

        public Builder setRootClass(Class<?> rootClass) {
            this.rootClass = rootClass;
            return this;
        }

        public QuickLinkContextConfiguration build() {
            if (strategies == null) {
                if (rootClass == null) {
                    throw new IllegalArgumentException("Either strategies or rootClass must be set.");
                }

                return new QuickLinkContextConfiguration(logFormatter, listenerConfiguration, runMode,
                        DefaultStrategies.strategies(ComponentScanScope.of(rootClass)));
            }
            return new QuickLinkContextConfiguration(logFormatter, listenerConfiguration, runMode, strategies);
        }
    }
}
