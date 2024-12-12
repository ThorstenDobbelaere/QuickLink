package cyclicalproject.service;

import framework.annotations.injection.semantic.Service;

@Service
public class CyclicalService3 {
    private final CyclicalService1 service1;

    public CyclicalService3(CyclicalService1 service1) {
        this.service1 = service1;
    }

    public String getName() {
        return service1.getName();
    }
}
