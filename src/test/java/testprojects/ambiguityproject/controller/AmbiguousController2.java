package testprojects.ambiguityproject.controller;

import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.mapping.OutputMapping;

@Controller("/ambiguous/call")
public class AmbiguousController2 {
    @OutputMapping
    public String call() {
        return "This method is also ambiguously mapped.";
    }
}
