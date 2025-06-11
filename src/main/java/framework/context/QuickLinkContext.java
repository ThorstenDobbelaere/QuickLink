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
    private final Package rootPackage;
    private final QuickLinkContextConfiguration configuration;

    private Instant lastTime;

    public QuickLinkContext(Class<?> root) {
        this(root, new QuickLinkContextConfiguration.Builder().build());
    }

    public String getPackageName() {
        return rootPackage.getName();
    }

    public QuickLinkContext(Class<?> root, QuickLinkContextConfiguration config) {
        this.rootPackage = root.getPackage();
        this.lastTime = Instant.now();
        cache = new ResultCache();
        this.configuration = config;
    }

    public RunMode getRunMode() {
        return configuration.runMode();
    }

    public LogFormatter getLogFormatter() {
        return configuration.logFormatter();
    }

    public ListenerConfiguration getListenerConfiguration() {
        return configuration.listenerConfiguration();
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
