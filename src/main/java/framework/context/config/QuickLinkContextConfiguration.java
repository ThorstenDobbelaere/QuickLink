package framework.context.config;

public record QuickLinkContextConfiguration(
        LogFormatter logFormatter,
        ListenerConfiguration listenerConfiguration,
        RunMode runMode
) {

    public static class Builder {
        private RunMode runMode = RunMode.HTTP;
        private LogFormatter logFormatter = null;
        private ListenerConfiguration listenerConfiguration = null;

        public Builder setRunMode(RunMode runMode) {
            this.runMode = runMode;
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

        public QuickLinkContextConfiguration build() {
            return new QuickLinkContextConfiguration(logFormatter, listenerConfiguration, runMode);
        }
    }
}
