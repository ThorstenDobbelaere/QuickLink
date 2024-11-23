package demo.example1.controller;

import demo.example1.service.CapsService;
import demo.example1.service.TestService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.HtmlMapping;

@Controller("/api/test")
public class TestController {
    private final TestService testService;
    private final CapsService capsService;

    public TestController(TestService testService, CapsService capsService) {
        this.testService = testService;
        this.capsService = capsService;
    }

    @HtmlMapping("/concat")
    public String test() {
        System.out.println("Running Concat");
        return testService.concatData();
    }

    @HtmlMapping("/caps")
    public String caps() {
        System.out.println("Running Caps");
        return capsService.capitalizeData();
    }
}
