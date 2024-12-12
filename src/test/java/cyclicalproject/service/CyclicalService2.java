package cyclicalproject.service;

import framework.annotations.injection.semantic.Service;

@Service
public class CyclicalService2 {
    private final CyclicalService3 service3;

    public CyclicalService2(CyclicalService3 service3) {
        this.service3 = service3;
    }

    public String getName() {
        return service3.getName();
    }
}
