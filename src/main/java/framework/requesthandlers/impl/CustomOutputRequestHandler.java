package framework.requesthandlers.impl;

import framework.exceptions.request.RequestException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.ResponseEntity;
import framework.requesthandlers.RequestHandler;

import java.util.function.Supplier;

public class CustomOutputRequestHandler extends RequestHandler {
    private final Supplier<ResponseEntity> responseSupplier;

    public CustomOutputRequestHandler(String mapping, Supplier<ResponseEntity> responseSupplier) {
        super(mapping);
        this.responseSupplier = responseSupplier;
    }

    @Override
    public HttpResponse handle(String input) throws RequestException {
        if(!input.isEmpty()) throw RequestException.noStringExpected(getMapping(), input);
        return new HttpResponse(responseSupplier.get());
    }
}
