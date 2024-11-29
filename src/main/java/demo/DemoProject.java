package demo;

import framework.QuickLink;
import framework.context.RunMode;
import framework.context.configurable.ListenerConfiguration;
import framework.context.configurable.QuickLinkContextConfiguration;

public class DemoProject {

    public static void main(String[] args) {
        int port = 5555;
        String shutdownUrl = "/stop";
        RunMode runMode = RunMode.HTTP;

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


        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
        listenerConfiguration.setPort(port);
        listenerConfiguration.setShutdownUrl(shutdownUrl);

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setHttpConfiguration(listenerConfiguration);
        config.setRunMode(runMode);

        QuickLink.run(DemoProject.class, config);
    }
}
