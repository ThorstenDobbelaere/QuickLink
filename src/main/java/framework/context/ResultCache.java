package framework.context;

import framework.setup.model.Component;
import framework.setup.model.MappedController;
import framework.request.handlers.MappedRequestHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultCache {
    private Set<Component> components;
    private Map<Class<?>, List<Method>> timedMethods;

    private Map<Component, Object> componentObjectMap;
    private Set<MappedController> mappedControllers;
    private List<MappedRequestHandler> mappedRequestHandlerList;

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

    public Map<Component, Object> getComponentObjectMap() {
        return componentObjectMap;
    }

    public void setComponentObjectMap(Map<Component, Object> componentObjectMap) {
        this.componentObjectMap = componentObjectMap;
    }

    public List<MappedRequestHandler> getRequestHandlerList() {
        return mappedRequestHandlerList;
    }

    public void setRequestHandlerList(List<MappedRequestHandler> mappedRequestHandlerList) {
        this.mappedRequestHandlerList = mappedRequestHandlerList;
    }

    public Map<Class<?>, List<Method>> getTimedMethods() {
        return timedMethods;
    }

    public void setTimedMethods(Map<Class<?>, List<Method>> timedMethods) {
        this.timedMethods = timedMethods;
    }
}
