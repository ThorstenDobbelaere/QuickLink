package demo.controller;

import demo.service.CapsService;
import demo.service.TestService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;

@Controller("/api/test")
public class TestController {
    private final TestService testService;
    private final CapsService capsService;

    public TestController(TestService testService, CapsService capsService) {
        this.testService = testService;
        this.capsService = capsService;
    }

    @OutputMapping("/concat")
    public String test() {
        //System.out.println("Running Concat");
        return testService.concatData();
    }

    @OutputMapping("/caps")
    public String caps() {
        System.out.println("Running Caps");
        return capsService.capitalizeData();
    }

    @OutputMapping("/count")
    public String count() {
        return Integer.toString(testService.count());
    }
}
