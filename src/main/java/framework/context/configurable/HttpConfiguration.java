package framework.context.configurable;

public class HttpConfiguration {
    private int port = 8080;
    private String shutdownUrl = "/shutdown";

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getShutdownUrl() {
        return shutdownUrl;
    }

    public void setShutdownUrl(String shutdownUrl) {
        this.shutdownUrl = shutdownUrl;
    }
}
