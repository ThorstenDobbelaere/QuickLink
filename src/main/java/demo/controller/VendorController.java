package demo.controller;

import demo.config.output.JsonOutputConverter;
import demo.model.Vendor;
import demo.service.SleepyService;
import demo.service.VendorService;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.InputMapping;
import framework.annotations.mapping.OutputMapping;
import framework.request.response.HttpStatus;
import framework.request.response.ResponseEntity;

import java.util.List;

@Controller("/vendors")
public class VendorController {
    private final VendorService vendorService;
    private final SleepyService sleepyService;

    public VendorController(VendorService vendorService, SleepyService sleepyService) {
        this.vendorService = vendorService;
        this.sleepyService = sleepyService;
    }

    @OutputMapping(outputConverter = JsonOutputConverter.class)
    public ResponseEntity getVendor(String name) {
        Vendor vendor = vendorService.findVendorByName(name);
        if (vendor == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(vendor, HttpStatus.OK);
    }

    @OutputMapping(value = "/all", outputConverter = JsonOutputConverter.class)
    public ResponseEntity getAllVendors(String username, String password) {
        if(!username.equals("admin") && !password.equals("admin")) {
            return new ResponseEntity(null, HttpStatus.FORBIDDEN);
        }

        List<Vendor> vendorList = vendorService.findAllVendors();
        return new ResponseEntity(vendorList, HttpStatus.OK);
    }

    @InputMapping(value = "/timed")
    public void sleep() {
        sleepyService.bigTask();
    }


}
