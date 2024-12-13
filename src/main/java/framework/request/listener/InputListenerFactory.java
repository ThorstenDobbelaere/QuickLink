package framework.request.listener;

import framework.context.QuickLinkContext;
import framework.context.RunMode;

public class InputListenerFactory {
    public static InputListener createInputListener(QuickLinkContext config) {
        RunMode runMode = config.getRunMode();

        return switch (runMode){
            case HTTP -> new SimpleHttpListener(config);
            case CONSOLE -> new ConsoleListener(config);
        };
    }
}
