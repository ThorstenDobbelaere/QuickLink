package demo;

import framework.QuickLink;
import framework.context.configurable.HttpConfiguration;
import framework.context.configurable.QuickLinkContextConfiguration;

public class TestProject {

    public static void main(String[] args) {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setPort(5555);

        QuickLinkContextConfiguration config = new QuickLinkContextConfiguration();
        config.setHttpConfiguration(httpConfiguration);

        QuickLink.run(TestProject.class, config);
    }
}
