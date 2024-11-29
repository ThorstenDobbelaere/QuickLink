package framework.request.response;

public enum ContentType {
    PLAIN("text/plain"),
    HTML("text/html"),
    JSON("application/json"),
    XML("application/xml");


    private final String httpString;

    public String getHttpString() {
        return httpString;
    }

    ContentType(String httpString) {
        this.httpString = httpString;
    }
}
