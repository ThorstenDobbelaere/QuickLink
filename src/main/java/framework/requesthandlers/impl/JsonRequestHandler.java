package framework.requesthandlers.impl;

import framework.exceptions.RequestException;
import framework.requesthandlers.RequestHandler;

import java.util.function.Supplier;

public class JsonRequestHandler extends RequestHandler<String> {
    private final Supplier<String> jsonSupplier;

    @Override
    public String handle(String input) throws RequestException {
        if(input != null && !input.isEmpty()){
            throw RequestException.noStringExpected(this.getMapping(), input);
        }

        return jsonSupplier.get();
    }

    public JsonRequestHandler(String mapping, Supplier<Object> supplier) {
        super(mapping);
        this.jsonSupplier = ()->supplier.get().toString();
    }
}
