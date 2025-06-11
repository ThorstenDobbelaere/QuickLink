package testprojects.cyclicalproject.controller;

import testprojects.cyclicalproject.service.CyclicalService1;
import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.mapping.OutputMapping;

@Controller
public class SimpleController {
    private final CyclicalService1 service1;

    public SimpleController(CyclicalService1 service1) {
        this.service1 = service1;
    }

    @OutputMapping("name")
    public String getName(){
        return service1.getName();
    }
}
