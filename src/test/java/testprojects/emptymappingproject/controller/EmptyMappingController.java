package testprojects.emptymappingproject.controller;

import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.mapping.OutputMapping;

@Controller
public class EmptyMappingController {
    @OutputMapping
    public String output() {
        return "This is a method with an empty mapping.";
    }
}
