package demo;

import framework.QuickLink;
import framework.context.configurable.HttpConfiguration;
import framework.context.configurable.QuickLinkContextConfiguration;

public class DemoProject {

    public static void main(String[] args) {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setPort(5555);
        httpConfiguration.setShutdownUrl("stop");

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setHttpConfiguration(httpConfiguration);

        QuickLink.run(DemoProject.class, config);
    }
}
