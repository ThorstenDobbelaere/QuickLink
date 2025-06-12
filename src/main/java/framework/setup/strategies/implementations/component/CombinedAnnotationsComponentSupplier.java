package framework.setup.strategies.implementations.component;

import component_scan.exceptions.DuplicateException;
import framework.setup.model.Component;
import framework.setup.strategies.contracts.ComponentSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CombinedAnnotationsComponentSupplier implements ComponentSupplier {
    private final Collection<ComponentSupplier> componentSuppliers;
    private final ComponentSupplier defaultSupplier;

    public CombinedAnnotationsComponentSupplier(
            Collection<ComponentSupplier> componentSuppliers,
            ComponentSupplier defaultSupplier
    ) {
        this.componentSuppliers = componentSuppliers;
        this.defaultSupplier = defaultSupplier;
    }


    private void checkForDuplicates(Collection<Component> components) {
        List<Component> duplicateComponents = components.stream()
                .filter(entry1->components.stream()
                        .filter(entry2-> entry1.getType().equals(entry2.getType())
                        ).count()>1)
                .distinct()
                .toList();

        if (!duplicateComponents.isEmpty()) {
            throw DuplicateException.duplicateComponent(duplicateComponents.getFirst().getType());
        }
    }

    private void applyDefaultConfigurations(Collection<Component> components) {
        Collection<Component> defaultComponents = defaultSupplier.getComponents();

        List<Component> defaultComponentsToAdd = defaultComponents.stream()
                .filter(entry1 -> components.stream().noneMatch(entry2 -> entry1.getType().equals(entry2.getType())))
                .toList();

        components.addAll(defaultComponentsToAdd);
    }

    @Override
    public Collection<Component> getComponents() {
        List<Component> components = componentSuppliers.stream()
                .flatMap(supplier -> supplier.getComponents().stream())
                .collect(Collectors.toCollection(ArrayList::new));
        applyDefaultConfigurations(components);
        checkForDuplicates(components);
        return components;
    }
}
