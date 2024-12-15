package demo.controller.dto;

public record WarehouseDto(
        String resourceName,
        double resourcePrice,
        String vendorName,
        String vendorAddress,
        double capacity,
        double content
) {}
