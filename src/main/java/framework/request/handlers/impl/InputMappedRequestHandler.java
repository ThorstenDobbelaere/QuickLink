package framework.request.handlers.impl;

import framework.exceptions.wrapper.HttpException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.request.input.InputScanners;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.request.handlers.MappedRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InputMappedRequestHandler extends MappedRequestHandler {
    private final Consumer<Object[]> callback;
    private final List<Class<?>> expectedClasses;
    private final List<String> paramNames;
    private static final Logger LOGGER = LoggerFactory.getLogger(InputMappedRequestHandler.class);

    public InputMappedRequestHandler(String mapping, Consumer<Object[]> callback, List<Class<?>> expectedClasses, List<String> paramNames) {
        super(mapping);
        this.callback = callback;
        this.expectedClasses = expectedClasses;
        this.paramNames = paramNames;
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

    @Override
    public String getMappingWithParams() {
        return String.format("%s/%s", getMapping(), paramNames.stream().map(s->String.format("{%s}", s)).collect(Collectors.joining("/")));
    }
}
