package framework.request.handlers;

import framework.exceptions.request.RequestException;
import framework.request.response.HttpResponse;

public abstract class MappedRequestHandler {
    private final String mapping;
    public abstract HttpResponse handle(String input) throws RequestException;

    protected MappedRequestHandler(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }

    public String getMappingWithParams(){
        return mapping;
    }
}
