package demo.controller;

import demo.model.Resource;
import demo.service.ResourceService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.IOMapping;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;

import java.util.List;

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

    // GET met name
    @OutputMapping("/name")
    public Resource getResourceByReferenceName(String name){
        return resourceService.getResourceByReferenceName(name);
    }

    // Zelfde mapping als getResource is niet mogelijk
    @OutputMapping("/all")
    public List<Resource> getAllResources(){
        return resourceService.getAllResources();
    }

    // IOMapping voor "POST" met response
    @IOMapping("/create-io")
    public Resource createResource(String name, String referenceName, double price){
        return resourceService.createResource(name, referenceName, price);
    }

    // InputMapping voor "POST" zonder response
    @InputMapping("/create-input")
    public void createResourceInput(String name, String referenceName, double price){
        resourceService.createResource(name, referenceName, price);
    }

    // Delete a resource
    @InputMapping("/delete")
    public void removeResource(String referenceName){
        resourceService.removeResource(referenceName);
    }
}
