package demo.repository;

import demo.model.Resource;
import framework.annotations.injection.semantic.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResourceRepository {
    private final Map<Integer, Resource> resources;
    private int nextId;

    public ResourceRepository() {
        resources = new HashMap<>();
        resources.put(1, new Resource("Iron Ore", "iron_ore", 10.0));
        resources.put(2, new Resource("Slag", "slag", 5.5));
        resources.put(3, new Resource("Coke", "coke", 3.5));
        nextId = 4;
    }

    public List<Resource> getResources() {return resources.values().stream().toList();}

    public void addResource(Resource resource) {
        resources.put(nextId, resource);
        nextId++;
    }

    public void deleteResource(String referenceName) {
        var optionalEntry = resources.entrySet()
                .stream()
                .filter(e->e.getValue().referenceName().equals(referenceName))
                .findFirst();
        if(optionalEntry.isEmpty()) {
            return;
        }
        int id = optionalEntry.get().getKey();
        resources.remove(id);
    }

    public Resource getResourceByReferenceName(String name) {
        return resources.values().stream().filter(resource -> resource.referenceName().equals(name)).findFirst().orElse(null);
    }

    public Resource getResource(int id) {
        return resources.get(id);
    }

    public int getIdForResource(String resourceName) {
        return resources.entrySet().stream().filter(e -> e.getValue().referenceName().equals(resourceName)).map(Map.Entry::getKey).findFirst().orElse(-1);
    }
}
