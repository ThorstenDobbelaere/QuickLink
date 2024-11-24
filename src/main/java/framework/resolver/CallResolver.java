package framework.resolver;

import framework.context.QuickLinkContext;
import framework.http.internal.HttpResponse;
import framework.http.responseentity.HttpStatus;
import framework.requesthandlers.RequestHandler;

import java.util.List;
import java.util.Optional;

public class CallResolver {
    private  static List<RequestHandler> requestHandlers;
    public static void setup(QuickLinkContext context) {
        requestHandlers = context.getCache().getRequestHandlerList();
    }

    public static HttpResponse handleCall(String input){
        if(requestHandlers == null) throw new NullPointerException("Handlers not initialized.");
        Optional<RequestHandler> optionalRequestHandler = requestHandlers
                .stream()
                .filter(requestHandler -> input.startsWith(requestHandler.getMapping()))
                .findAny();
        if(optionalRequestHandler.isEmpty()){
            return new HttpResponse(HttpStatus.NOT_FOUND);
        }
        RequestHandler requestHandler = optionalRequestHandler.get();
        return requestHandler.handle(input.substring(requestHandler.getMapping().length()));
    }
}
