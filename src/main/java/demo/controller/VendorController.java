package demo.controller;

import demo.config.types.JsonStringifier;
import demo.model.Vendor;
import demo.service.VendorService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;
import framework.http.responseentity.HttpStatus;
import framework.http.responseentity.ResponseEntity;

@Controller("/vendor")
public class VendorController {
    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @OutputMapping(value = "/get", stringifier = JsonStringifier.class)
    public ResponseEntity getVendor() {
        Vendor vendor = vendorService.findVendorByName("jane");
        return new ResponseEntity(vendor, HttpStatus.OK);
    }
}
