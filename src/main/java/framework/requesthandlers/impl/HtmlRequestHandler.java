package framework.requesthandlers.impl;

import framework.configurables.Stringifier;
import framework.exceptions.request.RequestException;
import framework.requesthandlers.RequestHandler;

import java.util.function.Supplier;

public class HtmlRequestHandler extends RequestHandler<String> {
    private final Supplier<Object> objectSupplier;
    private final Stringifier stringifier;

    @Override
    public String handle(String input) throws RequestException {
        if(input != null && !input.isEmpty()){
            throw RequestException.noStringExpected(this.getMapping(), input);
        }
        Object object = objectSupplier.get();
        return stringifier.stringify(object);
    }

    public HtmlRequestHandler(String mapping, Supplier<Object> supplier, Stringifier stringifier) {
        super(mapping);
        this.objectSupplier = supplier;
        this.stringifier = stringifier;
    }
}
