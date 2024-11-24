package framework.requesthandlers.impl;

import framework.exceptions.request.RequestException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.requesthandlers.RequestHandler;

import java.util.function.Consumer;

public class SetRequestHandler extends RequestHandler {
    private final Consumer<String> callback;

    public SetRequestHandler(String mapping, Consumer<String> callback) {
        super(mapping);
        this.callback = callback;
    }

    @Override
    public HttpResponse handle(String input) throws RequestException {
        callback.accept(input);
        return new HttpResponse(HttpStatus.ACCEPTED);
    }
}
