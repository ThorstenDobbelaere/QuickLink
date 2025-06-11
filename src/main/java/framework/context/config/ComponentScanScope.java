package framework.context.config;

import java.util.Collection;
import java.util.List;

public class ComponentScanScope {
    private final Collection<Class<?>> packageRootClasses;

    private ComponentScanScope(Collection<Class<?>> packageRootClasses) {
        this.packageRootClasses = packageRootClasses;
    }

    public Collection<Class<?>> getPackageRootClasses() {
        return packageRootClasses;
    }

    public static ComponentScanScope of(Class<?> packageRootClass) {
        return new ComponentScanScope(List.of(packageRootClass));
    }

    public static ComponentScanScope of(Class<?>... packageRootClasses) {
        return new ComponentScanScope(List.of(packageRootClasses));
    }
}
