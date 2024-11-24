package framework;

import framework.context.QuickLinkContext;
import framework.context.configurable.QuickLinkContextConfiguration;
import framework.http.listener.SimpleHttpListener;
import framework.resolver.CallResolver;
import framework.resolver.RequestMapper;
import framework.resolver.ObjectMapper;
import framework.resolver.ComponentScanner;
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

        ObjectMapper.mapObjectsAndControllers(context);
        printTimeStamp(context, "object and controller mapping");

        RequestMapper.mapRequests(context);
        printTimeStamp(context, "request mapping");

        CallResolver.setup(context);

        SimpleHttpListener listener = new SimpleHttpListener(context);
        printTimeStamp(context, "http listener");
        try{
            listener.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private QuickLink(){}
}
