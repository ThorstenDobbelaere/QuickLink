package framework.resolver;

import framework.context.QuickLinkContext;
import framework.exceptions.HttpException;
import framework.exceptions.request.RequestParameterScanningException;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.requesthandlers.RequestHandler;
import javassist.NotFoundException;

import java.util.NavigableMap;
import java.util.TreeMap;

public class CallResolver {
    private static NavigableMap<String, RequestHandler> requestHandlerMap;

    public static void setup(QuickLinkContext context) {
        requestHandlerMap = new TreeMap<>();
        for (RequestHandler handler : context.getCache().getRequestHandlerList()) {
            requestHandlerMap.put(handler.getMapping(), handler);
        }
    }

    private static String checkMapping(String url) {
        String mapping = requestHandlerMap.floorKey(url);
        if (mapping == null || !url.startsWith(mapping)) {
            throw new HttpException(new NotFoundException("Unable to map request."), HttpStatus.NOT_FOUND);
        }
        return mapping;
    }

    private static HttpResponse handleMappedRequest(String url, String mapping) {
        try{
            RequestHandler requestHandler = requestHandlerMap.get(mapping);
            return requestHandler.handle(url.substring(mapping.length()));
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }
    }

    public static HttpResponse handleCall(String url) throws HttpException {
        if (requestHandlerMap == null)
            throw new HttpException(new NullPointerException("Handlers not initialized."));

        try{
            String mapping = checkMapping(url);
            return handleMappedRequest(url, mapping);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
