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
    private final QuickLinkContext context;

    public QuickLink(QuickLinkContext context) {
        this.context = context;
    }

    private static void printTimeStamp(QuickLinkContext context, String description) {
        LOGGER.info("Finished {} in {} ms", description, context.getChrono());
    }

    public static void run(Class<?> root){
        QuickLinkContext context = new QuickLinkContext(root);
        new QuickLink(context).setup();
    }

    public static void run(Class<?> root, QuickLinkContextConfiguration configuration){
        QuickLinkContext context = new QuickLinkContext(root, configuration);
        new QuickLink(context).setup();
    }

    private void setup() {
        printTimeStamp(context, "context setup");
        this.scanComponents()
            .checkCycles()
            .instantiateSingletons()
            .mapControllers()
            .mapControllerMethods()
            .setupCallResolver()
            .startListening();
    }

    public QuickLink scanComponents() {
        ComponentScanner.scanComponentsAndInterceptables(context);
        printTimeStamp(context, "component and intercept method scanning");
        return this;
    }

    public QuickLink checkCycles() {
        GraphChecker.checkCycles(context);
        printTimeStamp(context, "cycle checking");
        return this;
    }

    public QuickLink instantiateSingletons() {
        InjectableFactory.instantiateSingletons(context);
        printTimeStamp(context, "injectable singleton instantiation");
        return this;
    }

    public QuickLink mapControllers() {
        ControllerMapper.mapControllersToUrls(context);
        printTimeStamp(context, "controller url mapping");
        return this;
    }

    public QuickLink mapControllerMethods() {
        ControllerMethodMapper.mapHandlersForRequests(context);
        printTimeStamp(context, "request handler url mapping");
        return this;
    }

    public QuickLink setupCallResolver() {
        CallResolver.setup(context);
        return this;
    }

    public void startListening() {
        InputListener listener = InputListenerFactory.createInputListener(context);
        printTimeStamp(context, "listener setup");
        try{
            listener.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
