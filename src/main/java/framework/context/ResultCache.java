package framework.context;

import framework.model.Component;
import framework.model.MappedController;
import framework.model.MappedMethod;
import framework.requesthandlers.RequestHandler;

import java.util.List;
import java.util.Set;

public class ResultCache {
    private Set<Component> components;

    private Set<MappedController> mappedControllers;
    private Set<MappedMethod> mappedMethods;
    private List<RequestHandler<?>> requestHandlerList;

    ResultCache(){}

    public Set<Component> getComponents() {
        return components;
    }

    public void setComponents(Set<Component> components) {
        this.components = components;
    }

    public Set<MappedController> getMappedControllers() {
        return mappedControllers;
    }

    public void setMappedControllers(Set<MappedController> mappedControllers) {
        this.mappedControllers = mappedControllers;
    }

    public Set<MappedMethod> getMappedMethods() {
        return mappedMethods;
    }

    public void setMappedMethods(Set<MappedMethod> mappedMethods) {
        this.mappedMethods = mappedMethods;
    }

    public List<RequestHandler<?>> getRequestHandlerList() {
        return requestHandlerList;
    }

    public void setRequestHandlerList(List<RequestHandler<?>> requestHandlerList) {
        this.requestHandlerList = requestHandlerList;
    }
}
