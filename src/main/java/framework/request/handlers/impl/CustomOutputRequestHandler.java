package framework.request.handlers.impl;

import framework.configurables.Stringifier;
import framework.exceptions.request.RequestException;
import framework.request.response.HttpResponse;
import framework.request.response.ResponseEntity;
import framework.request.handlers.RequestHandler;

import java.util.function.Supplier;

public class CustomOutputRequestHandler extends RequestHandler {
    private final Supplier<ResponseEntity> responseSupplier;
    private final Stringifier stringifier;

    public CustomOutputRequestHandler(String mapping, Supplier<ResponseEntity> responseSupplier, Stringifier stringifier) {
        super(mapping);
        this.responseSupplier = responseSupplier;
        this.stringifier = stringifier;
    }

    @Override
    public HttpResponse handle(String input) throws RequestException {
        if(!input.isEmpty()) throw RequestException.noStringExpected(getMapping(), input);
        return new HttpResponse(responseSupplier.get(), stringifier);
    }
}
