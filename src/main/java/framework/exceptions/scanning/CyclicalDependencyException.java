package framework.exceptions.scanning;

import java.util.Set;
import java.util.stream.Collectors;

public class CyclicalDependencyException extends RuntimeException {
    private final Set<Class<?>> classes;

    public CyclicalDependencyException(Set<Class<?>> classes) {
        super(String.format("Cyclical dependencies found:\n%s", classes.stream().map(Class::getName).collect(Collectors.joining("\n"))));
        this.classes = classes;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
