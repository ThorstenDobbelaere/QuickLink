package demo.repository;

import demo.model.Vendor;
import framework.annotations.injection.semantic.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VendorRepository {
    private final Map<Integer, Vendor> vendors;
    private int nextId;

    public VendorRepository() {
        this.vendors = new HashMap<>();
        vendors.put(1, new Vendor("Mark", "Wilgenlaan 12 - Amsterdam"));
        vendors.put(2, new Vendor("Bob", "Bergzicht 89 - Rotterdam"));
        vendors.put(3, new Vendor("John", "Kastanjedreef 45 - Utrecht"));
        vendors.put(4, new Vendor("Jane", "Zonnestraat 67 - Den Haag"));
        nextId = 5;
    }

    public List<Vendor> getVendors() {
        return new ArrayList<>(vendors.values());
    }

    public Vendor getVendor(int id) {
        return vendors.get(id);
    }

    public void addVendor(Vendor vendor) {
        vendors.put(nextId, vendor);
        nextId++;
    }

    public void removeVendor(int id) {
        vendors.remove(id);
    }

    public int getIdForVendor(String vendorName) {
        return vendors.entrySet().stream().filter(e -> e.getValue().name().equalsIgnoreCase(vendorName)).map(Map.Entry::getKey).findFirst().orElse(-1);
    }
}
