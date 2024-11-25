package framework.requesthandlers.impl;

import framework.exceptions.HttpException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.http.input.InputScanners;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.requesthandlers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class InputRequestHandler extends RequestHandler {
    private final Consumer<Object[]> callback;
    private final List<Class<?>> expectedClasses;
    private static final Logger LOGGER = LoggerFactory.getLogger(InputRequestHandler.class);

    public InputRequestHandler(String mapping, Consumer<Object[]> callback, List<Class<?>> expectedClasses) {
        super(mapping);
        this.callback = callback;
        this.expectedClasses = expectedClasses;
    }

    @Override
    public HttpResponse handle(String input) throws RequestException {
        try{
            Object[] parsedInputs = InputScanners.parseInput(input, expectedClasses);
            LOGGER.debug("Parsed inputs: {}", Arrays.stream(parsedInputs).toList());
            callback.accept(parsedInputs);
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }

        return new HttpResponse(HttpStatus.NO_CONTENT);
    }
}
