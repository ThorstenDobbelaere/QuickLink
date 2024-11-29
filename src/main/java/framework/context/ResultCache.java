package framework.context;

import framework.setup.model.Component;
import framework.setup.model.MappedController;
import framework.request.handlers.RequestHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultCache {
    private Set<Component> components;

    private Map<Class<?>, List<Method>> timedMethods;
    private Set<MappedController> mappedControllers;
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

    public List<RequestHandler> getRequestHandlerList() {
        return requestHandlerList;
    }

    public void setRequestHandlerList(List<RequestHandler> requestHandlerList) {
        this.requestHandlerList = requestHandlerList;
    }

    public Map<Class<?>, List<Method>> getTimedMethods() {
        return timedMethods;
    }

    public void setTimedMethods(Map<Class<?>, List<Method>> timedMethods) {
        this.timedMethods = timedMethods;
    }
}
