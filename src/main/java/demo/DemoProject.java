package demo;

import framework.QuickLink;
import framework.context.RunMode;
import framework.context.configurable.ListenerConfiguration;
import framework.context.configurable.QuickLinkContextConfiguration;

import java.util.logging.Logger;

public class DemoProject {
    private static final Logger LOGGER = Logger.getLogger(DemoProject.class.getName());

    public static void main(String[] args) {
        int port = 5555;
        String shutdownUrl = "/stop";
        RunMode runMode = RunMode.HTTP;

        try{
            for(String arg : args) {
                if(arg.startsWith("--port=")) {
                    port = Integer.parseInt(arg.split("=")[1]);
                }
                if(arg.startsWith("--shutdown=")) {
                    shutdownUrl = arg.split("=")[1];
                }
                if(arg.equals("--console")){
                    runMode = RunMode.CONSOLE;
                }
            }
        } catch (Exception e){
            LOGGER.warning("Encountered an error while reading arguments: " + e.getMessage());
            LOGGER.warning("Using default settings");
        }



        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
        listenerConfiguration.setPort(port);
        listenerConfiguration.setShutdownUrl(shutdownUrl);

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setListenerConfiguration(listenerConfiguration);
        config.setRunMode(runMode);

        QuickLink.run(DemoProject.class, config);
    }
}
