package testprojects.emptymappingproject.controller;

import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;

@Controller
public class EmptyMappingController {
    @OutputMapping
    public String output() {
        return "This is a method with an empty mapping.";
    }
}
