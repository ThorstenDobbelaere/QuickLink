package framework.context.configurable;

public class QuickLinkContextConfiguration {
    private LogFormatter logFormatter = null;
    private HttpConfiguration httpConfiguration = null;

    public LogFormatter getLogFormatter() {
        return logFormatter;
    }

    public void setLogFormatter(LogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

    public HttpConfiguration getHttpConfiguration() {
        return httpConfiguration;
    }

    public void setHttpConfiguration(HttpConfiguration httpConfiguration) {
        this.httpConfiguration = httpConfiguration;
    }
}
