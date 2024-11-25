package framework.requesthandlers.impl;

import framework.configurables.Stringifier;
import framework.exceptions.HttpException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.http.input.InputScanners;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.http.responseentity.ResponseEntity;
import framework.requesthandlers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CustomIORequestHandler extends RequestHandler {
    private final Stringifier stringifier;
    private final Function<Object[], ResponseEntity> callback;
    private final List<Class<?>> expectedClasses;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIORequestHandler.class);


    @Override
    public HttpResponse handle(String input) throws RequestException {
        try{
            Object[] parsedInputs = InputScanners.parseInput(input, expectedClasses);
            LOGGER.debug("Parsed inputs: {}", Arrays.stream(parsedInputs).toList());
            ResponseEntity entity = callback.apply(parsedInputs);
            return new HttpResponse(entity, stringifier);
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }
    }

    public CustomIORequestHandler(String mapping, Stringifier stringifier, Function<Object[], ResponseEntity> callback, List<Class<?>> expectedClasses) {
        super(mapping);
        this.stringifier = stringifier;
        this.callback = callback;
        this.expectedClasses = expectedClasses;
    }
}
