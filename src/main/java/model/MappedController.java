package model;

public class MappedController {
    private final String mapping;
    private final Object controller;

    public MappedController(String mapping, Object controller) {
        this.mapping = mapping;
        this.controller = controller;
    }

    public String getMapping() {
        return mapping;
    }

    public Object getController() {
        return controller;
    }
}
