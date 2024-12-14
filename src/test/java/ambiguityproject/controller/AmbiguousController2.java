package ambiguityproject.controller;

import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;

@Controller("/ambiguous/call")
public class AmbiguousController2 {
    @OutputMapping
    public String call() {
        return "This method is also ambiguously mapped.";
    }
}
