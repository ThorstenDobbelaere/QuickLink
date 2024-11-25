package demo.repository;

import demo.model.Warehouse;
import framework.annotations.injection.semantic.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseRepository {
    private final List<Warehouse> warehouses;

    public WarehouseRepository() {
        warehouses = new ArrayList<>(List.of(
                new Warehouse(1, 1, 500, 200),
                new Warehouse(1, 2, 600, 250),
                new Warehouse(1, 3, 700, 150),
                new Warehouse(1, 4, 550, 150),
                new Warehouse(2, 1, 650, 200),
                new Warehouse(2, 2, 750, 300),
                new Warehouse(2, 3, 100, 60)
        ));
    }

    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void addWarehouse(Warehouse warehouse) {
        warehouses.add(warehouse);
    }

    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse);
    }

    public Optional<Warehouse> getWarehouse(int vendorId, int resourceId) {
        return warehouses.stream().filter(warehouse -> warehouse.resourceId() == resourceId && warehouse.vendorId() == vendorId).findFirst();
    }
}
