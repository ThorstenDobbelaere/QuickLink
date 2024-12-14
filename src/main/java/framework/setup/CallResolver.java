package framework.setup;

import framework.configurables.conversions.impl.OutputConverterDefaultImpl;
import framework.context.QuickLinkContext;
import framework.exceptions.wrapper.HttpException;
import framework.exceptions.internal.InternalException;
import framework.exceptions.request.RequestException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.request.handlers.MappedRequestHandler;
import framework.request.response.ResponseEntity;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NavigableMap;
import java.util.TreeMap;

public class CallResolver {
    private static NavigableMap<String, MappedRequestHandler> requestHandlerMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(CallResolver.class);

    public static void setup(QuickLinkContext context) {
        requestHandlerMap = new TreeMap<>();
        for (MappedRequestHandler handler : context.getCache().getRequestHandlerList()) {
            requestHandlerMap.put(handler.getMapping(), handler);
        }
    }

    private static String checkMapping(String url) {
        String mapping = requestHandlerMap.floorKey(url);

        if (!doesUrlMatchMapping(url, mapping)) {
            throw new HttpException(new NotFoundException("Unable to map request"), HttpStatus.BAD_REQUEST);
        }
        return mapping;
    }

    private static boolean doesUrlMatchMapping(String url, String mapping) {
        if(mapping == null) {
            return false;
        }

        if(url.equals(mapping)) {
            return true;
        }

        if(!url.startsWith(mapping)) {
            return false;
        }

        try{
            char slash = url.charAt(mapping.length());
            return slash == '/';
        } catch (IndexOutOfBoundsException e) {
            throw new InternalException("Slash check out of bounds");
        }
    }

    private static HttpResponse handleMappedRequest(String url, String mapping) {
        try{
            MappedRequestHandler mappedRequestHandler = requestHandlerMap.get(mapping);
            return mappedRequestHandler.handle(url.substring(mapping.length()));
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }
    }

    private static HttpResponse tryHandleCall(String url) throws HttpException {
        if (requestHandlerMap == null){
            throw new HttpException(new NullPointerException("Handlers not initialized."));
        }

        try{
            String mapping = checkMapping(url);
            return handleMappedRequest(url, mapping);
        } catch (HttpException e) {
            throw e;
        } catch (RequestParameterScanningException | RequestException e) {
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new HttpException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static HttpResponse handleCall(String url) {
        try{
            return tryHandleCall(checkMapping(url));
        } catch (HttpException e) {
            LOGGER.error("HTTP Error occurred: {}. Returning status code {}", e.getMessage(), e.getStatus());
            ResponseEntity entity = new ResponseEntity(String.format("An error occurred while handling your request:\n%s", e.getMessage()), e.getStatus());
            return new HttpResponse(entity, new OutputConverterDefaultImpl());
        }
    }
}
