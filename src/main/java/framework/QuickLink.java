package framework;

import framework.context.QuickLinkContext;
import framework.resolver.CallResolver;
import framework.resolver.RequestMapper;
import framework.resolver.ObjectMapper;
import framework.resolver.ComponentScanner;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class QuickLink {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuickLink.class.getName());

    private static void printTimeStamp(QuickLinkContext context, String description) {
        LOGGER.info("Finished {} in {} ms\n\n", description, context.getChrono());
    }

    public static void run(Class<?> root){
        QuickLinkContext context = new QuickLinkContext(root);
        printTimeStamp(context, "context setup");

        ComponentScanner.fillComponentSet(context);
        printTimeStamp(context, "component scanning");

        ObjectMapper.init(context);
        printTimeStamp(context, "object mapping");

        RequestMapper.init(context);
        printTimeStamp(context, "request mapping");

        CallResolver.setup(context);
        // (Set up listener)
    }

    public static Object handleCall(String path){
        return CallResolver.handleCall(path);
    }

    private QuickLink(){}
}
