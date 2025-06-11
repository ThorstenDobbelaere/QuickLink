package framework.context.config;

public record ListenerConfiguration(int port, String shutdownUrl) {

    public static class Builder {
        private int port = 8080;
        private String shutdownUrl = "/shutdown";

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setShutdownUrl(String shutdownUrl) {
            if (!shutdownUrl.startsWith("/"))
                this.shutdownUrl = "/" + shutdownUrl;
            else
                this.shutdownUrl = shutdownUrl;
            return this;
        }

        public ListenerConfiguration build() {
            return new ListenerConfiguration(port, shutdownUrl);
        }
    }
}
