package demo.controller;

import demo.config.types.JsonStringifier;
import demo.config.types.ResourceToHtmlStringifier;
import demo.model.Warehouse;
import demo.service.WarehouseService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.IOMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseController.class);

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }


    @IOMapping(value = "/find", stringifier = JsonStringifier.class)
    public Warehouse findWarehouse(String vendorName, String resourceName){
        return warehouseService.getWarehouse(vendorName, resourceName);
    }

    @IOMapping(value = "/log", stringifier = ResourceToHtmlStringifier.class)
    public String log(String message){
        LOGGER.info("{}", message);
        return String.format("Logged '%s'", message);
    }


}
