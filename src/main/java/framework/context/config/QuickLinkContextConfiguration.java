package framework.context.config;

import framework.context.RunMode;

public class QuickLinkContextConfiguration {
    private LogFormatter logFormatter = null;
    private ListenerConfiguration listenerConfiguration = null;
    private RunMode runMode = RunMode.HTTP;

    public RunMode getRunMode() {
        return runMode;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }

    public LogFormatter getLogFormatter() {
        return logFormatter;
    }

    public void setLogFormatter(LogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

    public ListenerConfiguration getListenerConfiguration() {
        return listenerConfiguration;
    }

    public void setListenerConfiguration(ListenerConfiguration listenerConfiguration) {
        this.listenerConfiguration = listenerConfiguration;
    }
}
