package demo.service;

import demo.model.Warehouse;
import demo.repository.ResourceRepository;
import demo.repository.VendorRepository;
import demo.repository.WarehouseRepository;
import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;

import java.util.Optional;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final VendorRepository vendorRepository;
    private final ResourceRepository resourceRepository;

    public WarehouseService(WarehouseRepository warehouseRepository, VendorRepository vendorRepository, ResourceRepository resourceRepository) {
        this.warehouseRepository = warehouseRepository;
        this.vendorRepository = vendorRepository;
        this.resourceRepository = resourceRepository;
    }

    @Timed
    public Warehouse getWarehouse(String vendorName, String resourceName) {
        int vendorId = vendorRepository.getIdForVendor(vendorName);
        int resourceId = resourceRepository.getIdForResource(resourceName);
        Optional<Warehouse> optionalWarehouse = warehouseRepository.getWarehouse(vendorId, resourceId);
        return optionalWarehouse.orElse(null);
    }
}
