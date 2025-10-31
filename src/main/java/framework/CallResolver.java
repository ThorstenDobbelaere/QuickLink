package framework;

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

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CallResolver {
    private final NavigableMap<String, MappedRequestHandler> requestHandlerMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(CallResolver.class);
    private static CallResolver callResolver;

    public CallResolver(List<MappedRequestHandler> requestHandlers) {
        this.requestHandlerMap = requestHandlers
            .stream()
            .collect(
                Collectors.toMap(
                    MappedRequestHandler::getMapping,
                    mappedRequestHandler -> mappedRequestHandler,
                    (existing, override) -> override,
                    TreeMap::new
                )
            );
    }

    @Deprecated
    public static void setup(QuickLinkContext context) {
        callResolver = new CallResolver(context.getCache().getRequestHandlerList());
    }

    private String checkMapping(String url) {
        String mapping = requestHandlerMap.floorKey(url);

        if (!doesUrlMatchMapping(url, mapping)) {
            throw new HttpException(new NotFoundException("Unable to map request"), HttpStatus.BAD_REQUEST);
        }

        LOGGER.debug("Mapped input {} to {}", url, mapping);

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

    private HttpResponse handleMappedRequest(String url, String mapping) {
        try{
            MappedRequestHandler mappedRequestHandler = requestHandlerMap.get(mapping);
            String args = url.substring(mapping.length());
            LOGGER.debug("Mapping = {}, Args = {}", mapping, args);

            return mappedRequestHandler.handle(args);
        } catch (RequestParameterScanningException e){
            throw new HttpException(e, HttpStatus.BAD_REQUEST);
        }
    }

    private HttpResponse tryHandleCall(String url) throws HttpException {
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

    public HttpResponse handleCall(String url) {
        try{
            return tryHandleCall(url);
        } catch (HttpException e) {
            LOGGER.error("HTTP Error occurred: {}. Returning status code {}", e.getMessage(), e.getStatus());
            ResponseEntity entity = new ResponseEntity(String.format("An error occurred while handling your request:%n%s", e.getMessage()), e.getStatus());
            return new HttpResponse(entity, new OutputConverterDefaultImpl());
        }
    }

    @Deprecated
    public static HttpResponse handleCallStatic(String url) {
        return callResolver.handleCall(url);
    }
}
