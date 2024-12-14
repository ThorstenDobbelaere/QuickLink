package ambiguityproject.controller;

import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;

@Controller("/ambiguous")
public class AmbiguousController1 {

    @OutputMapping("/call")
    public String ambiguousMethod() {
        return "This is method is mapped ambiguously.";
    }
}
