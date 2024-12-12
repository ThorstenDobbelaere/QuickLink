package framework.request.handlers;

import framework.exceptions.request.RequestException;
import framework.request.response.HttpResponse;

public abstract class RequestHandler{
    private final String mapping;
    public abstract HttpResponse handle(String input) throws RequestException;

    protected RequestHandler(String mapping) {
        this.mapping = mapping;
    }

    public String getMapping() {
        return mapping;
    }

    public String getMappingWithParams(){
        return mapping;
    }
}
