package demo.controller;

import demo.config.output.JsonOutputConverter;
import demo.model.Vendor;
import demo.service.VendorService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;
import framework.request.response.HttpStatus;
import framework.request.response.ResponseEntity;

@Controller("/vendor")
public class VendorController {
    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @OutputMapping(value = "/get", outputConverter = JsonOutputConverter.class)
    public ResponseEntity getVendor(String name) {
        Vendor vendor = vendorService.findVendorByName(name);
        if (vendor == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(vendor, HttpStatus.OK);
    }
}
