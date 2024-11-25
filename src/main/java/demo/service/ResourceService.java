package demo.service;

import demo.model.Resource;
import demo.repository.ResourceRepository;
import framework.annotations.injection.semantic.Service;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource getResource(int id) {
        return resourceRepository.getResource(id);
    }
}
