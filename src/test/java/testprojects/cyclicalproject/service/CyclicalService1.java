package testprojects.cyclicalproject.service;

import component_scan.annotations.injection.semantic.Service;

@Service
public class CyclicalService1 {
    private final CyclicalService2 cyclicalService2;

    public CyclicalService1(CyclicalService2 service2) {
        this.cyclicalService2 = service2;
    }

    public String getName(){
        return cyclicalService2.getName();
    }
}
