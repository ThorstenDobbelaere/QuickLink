package demo.service;

import demo.controller.dto.WarehouseDto;
import demo.model.Resource;
import demo.model.Vendor;
import demo.model.Warehouse;
import demo.repository.ResourceRepository;
import demo.repository.VendorRepository;
import demo.repository.WarehouseRepository;
import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;

import java.util.List;
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
    public WarehouseDto getWarehouse(String vendorName, String resourceName) {
        int vendorId = vendorRepository.getIdForVendor(vendorName);
        int resourceId = resourceRepository.getIdForResource(resourceName);

        Optional<Warehouse> optionalWarehouse = warehouseRepository.getWarehouse(vendorId, resourceId);
        if (optionalWarehouse.isEmpty()) {
            return null;
        }
        Warehouse warehouse = optionalWarehouse.get();
        return mapToDto(warehouse);
    }

    @Timed
    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.getWarehouses()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private WarehouseDto mapToDto(Warehouse warehouse) {
        Vendor vendor = vendorRepository.getVendor(warehouse.vendorId());
        Resource resource = resourceRepository.getResource(warehouse.resourceId());
        return new WarehouseDto(
                resource.name(),
                resource.price(),
                vendor.name(),
                vendor.address(),
                warehouse.capacity(),
                warehouse.content()
        );
    }
}
