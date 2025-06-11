package framework.context.state;

import framework.context.config.QuickLinkStrategies;
import framework.setup.model.Component;
import framework.setup.model.MappedController;
import framework.request.handlers.MappedRequestHandler;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;
import framework.setup.strategies.DefaultStrategies;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultCache {
    private Collection<Component> components;
    private Collection<InjectableClassWithInterceptedMethods<?>> timedMethods;

    private Map<Component, Object> componentObjectMap;
    private Set<MappedController> mappedControllers;
    private List<MappedRequestHandler> mappedRequestHandlerList;

    public Collection<Component> getComponents() {
        return components;
    }

    public void setComponents(Collection<Component> components) {
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

    public Collection<InjectableClassWithInterceptedMethods<?>> getTimedMethods() {
        return timedMethods;
    }

    public void setTimedMethods(Collection<InjectableClassWithInterceptedMethods<?>> timedMethods) {
        this.timedMethods = timedMethods;
    }

    public void applyComponentScan(QuickLinkStrategies strategies) {

    }
}
