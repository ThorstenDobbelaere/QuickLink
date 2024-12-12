package framework;

import framework.context.QuickLinkContext;
import framework.context.config.QuickLinkContextConfiguration;
import framework.request.listener.InputListener;
import framework.request.listener.InputListenerFactory;
import framework.setup.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public class QuickLink {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuickLink.class.getName());

    private static void printTimeStamp(QuickLinkContext context, String description) {
        LOGGER.info("Finished {} in {} ms", description, context.getChrono());
    }

    public static void run(Class<?> root){
        QuickLinkContext context = new QuickLinkContext(root);
        setup(context);
    }

    public static void run(Class<?> root, QuickLinkContextConfiguration configuration){
        QuickLinkContext context = new QuickLinkContext(root, configuration);
        setup(context);
    }

    private static void setup(QuickLinkContext context) {
        printTimeStamp(context, "context setup");

        ComponentScanner.scanComponentsAndInterceptables(context);
        printTimeStamp(context, "component scanning");

        GraphChecker.checkCycles(context);
        printTimeStamp(context, "cycle checking");

        ObjectMapper.mapObjectsAndControllers(context);
        printTimeStamp(context, "object and controller mapping");

        RequestMapper.mapRequests(context);
        printTimeStamp(context, "request mapping");

        CallResolver.setup(context);

        InputListener listener = InputListenerFactory.getInputListener(context);
        printTimeStamp(context, "listener setup");
        try{
            listener.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private QuickLink(){}
}
