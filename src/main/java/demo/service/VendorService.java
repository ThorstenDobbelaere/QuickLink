package demo.service;

import demo.model.Vendor;
import demo.repository.VendorRepository;
import framework.annotations.clarification.PrimaryConstructor;
import framework.annotations.injection.semantic.Service;

import java.util.List;

@Service
public class VendorService {
    private final VendorRepository vendorRepository;

    // Another constructor for ambiguity
    public VendorService(){
        this.vendorRepository = new VendorRepository();
    }

    @PrimaryConstructor
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor findVendorByName(String name) {
        int id = vendorRepository.getIdForVendor(name);
        return vendorRepository.getVendor(id);
    }

    public List<Vendor> findAllVendors() {
        return vendorRepository.getVendors();
    }
}
