package testprojects.ambiguityproject.controller;

import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.mapping.OutputMapping;

@Controller("/ambiguous")
public class AmbiguousController1 {

    @OutputMapping("/call")
    public String ambiguousMethod() {
        return "This is method is mapped ambiguously.";
    }
}
