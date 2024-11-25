package framework.requesthandlers.impl;

import framework.configurables.Stringifier;
import framework.exceptions.request.RequestException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.http.responseentity.ResponseEntity;
import framework.requesthandlers.RequestHandler;

import java.util.function.Supplier;

public class OutputRequestHandler extends RequestHandler {
    private final Supplier<Object> objectSupplier;
    private final Stringifier stringifier;


    @Override
    public HttpResponse handle(String input) throws RequestException {
        if(input != null && !input.isEmpty()){
            throw RequestException.noStringExpected(this.getMapping(), input);
        }
        Object object = objectSupplier.get();
        if(object == null){
            return new HttpResponse(HttpStatus.NOT_FOUND);
        }
        ResponseEntity responseEntity = new ResponseEntity(object, HttpStatus.OK);
        return new HttpResponse(responseEntity, stringifier);
    }

    public OutputRequestHandler(String mapping, Supplier<Object> supplier, Stringifier stringifier) {
        super(mapping);
        this.objectSupplier = supplier;
        this.stringifier = stringifier;
    }
}
