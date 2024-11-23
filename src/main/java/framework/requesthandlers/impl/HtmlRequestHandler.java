package framework.requesthandlers.impl;

import framework.exceptions.RequestException;
import framework.requesthandlers.RequestHandler;

import java.util.function.Supplier;

public class HtmlRequestHandler extends RequestHandler<String> {
    private final Supplier<String> htmlSupplier;

    @Override
    public String handle(String input) throws RequestException {
        if(input != null && !input.isEmpty()){
            throw RequestException.noStringExpected(this.getMapping(), input);
        }
        return htmlSupplier.get();
    }

    public HtmlRequestHandler(String mapping, Supplier<String> supplier) {
        super(mapping);
        this.htmlSupplier = supplier;
    }
}
