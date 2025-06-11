package framework.context;

import framework.context.config.ListenerConfiguration;
import framework.context.config.LogFormatter;
import framework.context.config.QuickLinkContextConfiguration;
import framework.exceptions.internal.NoSuchComponentException;
import framework.exceptions.internal.ComponentCastError;
import framework.setup.model.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public class QuickLinkContext {
    private final ResultCache cache;
    private final String packageName;

    private LogFormatter logFormatter = new LogFormatter();
    private ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
    private RunMode runMode;
    private Instant lastTime;

    public QuickLinkContext(Class<?> root) {
        this.packageName = root.getPackage().getName();
        this.lastTime = Instant.now();
        cache = new ResultCache();
        runMode = RunMode.HTTP;
    }

    public String getPackageName() {
        return packageName;
    }

    public QuickLinkContext(Class<?> root, QuickLinkContextConfiguration config) {
        this(root);

        if(config.getLogFormatter() != null)
            this.logFormatter = config.getLogFormatter();

        if(config.getListenerConfiguration() != null)
            this.listenerConfiguration = config.getListenerConfiguration();

        this.runMode = config.getRunMode();
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public LogFormatter getLogFormatter() {
        return logFormatter;
    }

    public ListenerConfiguration getListenerConfiguration() {
        return listenerConfiguration;
    }

    public long getChrono() {
        Instant now = Instant.now();
        Instant lastInstant = this.lastTime;
        this.lastTime = now;
        return now.toEpochMilli() - lastInstant.toEpochMilli();
    }

    public ResultCache getCache() {
        return cache;
    }

    public <T> T getInstanceOfType(Class<T> type) {
        Collection<Component> components = cache.getComponents();
        Optional<Component> optionalComponent = components.stream().filter(c -> c.getType().equals(type)).findFirst();
        if(optionalComponent.isEmpty()) throw new NoSuchComponentException(type);
        Object result = optionalComponent.get().getInstance();
        if(type.isInstance(result)) return type.cast(result);
        throw new ComponentCastError("Unable to cast component from " + result.getClass() + " to " + type);
    }
}
