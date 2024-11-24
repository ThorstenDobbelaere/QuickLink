package framework.context;

import framework.resolver.model.Component;
import framework.resolver.model.MappedController;
import framework.resolver.model.MappedMethod;
import framework.requesthandlers.RequestHandler;

import java.util.List;
import java.util.Set;

public class ResultCache {
    private Set<Component> components;

    private Set<MappedController> mappedControllers;
    private Set<MappedMethod> mappedMethods;
    private List<RequestHandler> requestHandlerList;

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

    public void setMappedMethods(Set<MappedMethod> mappedMethods) {
        this.mappedMethods = mappedMethods;
    }

    public List<RequestHandler> getRequestHandlerList() {
        return requestHandlerList;
    }

    public void setRequestHandlerList(List<RequestHandler> requestHandlerList) {
        this.requestHandlerList = requestHandlerList;
    }
}
