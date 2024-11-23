package framework.requesthandlers.impl;

import framework.exceptions.request.RequestException;
import framework.requesthandlers.RequestHandler;

import java.util.function.Consumer;

public class SetRequestHandler extends RequestHandler<Void> {
    private final Consumer<String> callback;

    public SetRequestHandler(String mapping, Consumer<String> callback) {
        super(mapping);
        this.callback = callback;
    }

    @Override
    public Void handle(String input) throws RequestException {
        callback.accept(input);
        return null;
    }
}
