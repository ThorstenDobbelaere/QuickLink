package framework.request.handlers.impl;

import framework.configurables.conversions.OutputConverter;
import framework.exceptions.HttpException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.request.input.InputScanners;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.request.response.ResponseEntity;
import framework.request.handlers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IORequestHandler extends RequestHandler {
    private final OutputConverter outputConverter;
    private final Function<Object[], Object> callback;
    private final List<String> paramNames;
    private final List<Class<?>> expectedClasses;
    private static final Logger LOGGER = LoggerFactory.getLogger(IORequestHandler.class);


    @Override
    public HttpResponse handle(String input) throws RequestException {
        try{
            Object[] parsedInputs = InputScanners.parseInput(input, expectedClasses);
            LOGGER.debug("Parsed inputs: {}", Arrays.stream(parsedInputs).toList());

            Object result = callback.apply(parsedInputs);
            ResponseEntity entity = new ResponseEntity(result, HttpStatus.OK);
            return new HttpResponse(entity, outputConverter);
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }
    }

    public IORequestHandler(String mapping, OutputConverter outputConverter, Function<Object[], Object> callback, List<Class<?>> expectedClasses, List<String> paramNames) {
        super(mapping);
        this.outputConverter = outputConverter;
        this.callback = callback;
        this.paramNames = paramNames;
        this.expectedClasses = expectedClasses;
    }

    @Override
    public String getMappingWithParams() {
        return String.format("%s/%s", getMapping(), paramNames.stream().map(s->String.format("{%s}", s)).collect(Collectors.joining("/")));
    }

}
