package demo.controller;

import demo.config.types.ResourceToHtmlOutputConverter;
import demo.model.Resource;
import demo.service.ResourceService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.OutputMapping;

@Controller
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @OutputMapping(value="/resource", outputConverter = ResourceToHtmlOutputConverter.class)
    public Resource getResource(int id){
        return resourceService.getResource(id);
    }

    @IOMapping("/create-resource")
    public Resource createResource(String name, String referenceName, double description){
        return resourceService.createResource(name, referenceName, description);
    }
}
