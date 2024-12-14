package demo.controller;

import demo.model.Resource;
import demo.service.ResourceService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;

@Controller("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    // OutputMapping voor "GET"
    @OutputMapping
    public Resource getResource(int id){
        return resourceService.getResource(id);
    }

    // IOMapping voor "POST" met response
    @IOMapping("/create-io")
    public Resource createResource(String name, String referenceName, double price){
        return resourceService.createResource(name, referenceName, price);
    }

    // InputMapping voor "POST" zonder response
    @InputMapping("/create-input")
    public void createResourceInput(int id, String name, String referenceName, double price){
        resourceService.createResource(name, referenceName, price);
    }
}
