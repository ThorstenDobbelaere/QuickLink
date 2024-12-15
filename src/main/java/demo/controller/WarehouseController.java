package demo.controller;

import demo.config.output.JsonOutputConverter;
import demo.config.output.WarehouseToHtmlOutputConverter;
import demo.controller.dto.WarehouseDto;
import demo.service.WarehouseService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;

import java.util.List;

@Controller("/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // Default output modifier: yellow text in console, no changes in
    @OutputMapping
    public WarehouseDto findWarehouse(String vendorName, String resourceName){
        return warehouseService.getWarehouse(vendorName, resourceName);
    }

    // Json output modifier
    @OutputMapping(value = "/as-json", outputConverter = JsonOutputConverter.class)
    public WarehouseDto findWarehouseJson(String vendorName, String resourceName){
        return warehouseService.getWarehouse(vendorName, resourceName);
    }

    // Warehouse-specific HTML output converter
    @OutputMapping(value = "/as-html", outputConverter = WarehouseToHtmlOutputConverter.class)
    public WarehouseDto findWarehouseHtml(String vendorName, String resourceName){
        return warehouseService.getWarehouse(vendorName, resourceName);
    }

    // All warehouses in JSON
    @OutputMapping(value = "/all", outputConverter = JsonOutputConverter.class)
    public List<WarehouseDto> findAllWarehouses(){
        return warehouseService.getAllWarehouses();
    }


}
