package demo.controller;

import demo.config.types.ResourceToHtmlStringifier;
import demo.model.Resource;
import demo.service.ResourceService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.IOMapping;

@Controller
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @IOMapping(value="/resource", stringifier = ResourceToHtmlStringifier.class)
    public Resource getResource(int id){
        return resourceService.getResource(id);
    }
}
