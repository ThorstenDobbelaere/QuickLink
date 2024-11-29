package framework.context;

import framework.context.configurable.ListenerConfiguration;
import framework.context.configurable.LogFormatter;
import framework.context.configurable.QuickLinkContextConfiguration;
import framework.exceptions.NoSuchComponentException;
import framework.exceptions.internal.ComponentCastError;
import framework.setup.model.Component;
import org.reflections.Reflections;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public class QuickLinkContext {
    private final ResultCache cache;
    private final ReflectionContext reflectionContext;
    private Instant lastTime;
    private LogFormatter logFormatter = new LogFormatter();
    private ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
    private RunMode runMode;

    public QuickLinkContext(Class<?> root) {
        String packageName = root.getPackage().getName();
        this.lastTime = Instant.now();
        cache = new ResultCache();
        reflectionContext = new ReflectionContext();
        Reflections reflections = new Reflections(packageName);
        reflectionContext.setProjectReflections(reflections);
        runMode = RunMode.HTTP;
    }

    public QuickLinkContext(Class<?> root, QuickLinkContextConfiguration config) {
        this(root);

        LogFormatter logFormatter = config.getLogFormatter();
        if(logFormatter != null)
            this.logFormatter = logFormatter;

        ListenerConfiguration listenerConfiguration = config.getListenerConfiguration();
        if(listenerConfiguration != null)
            this.listenerConfiguration = listenerConfiguration;

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
        Instant lastTime = this.lastTime;
        this.lastTime = now;
        return now.toEpochMilli() - lastTime.toEpochMilli();
    }

    public ResultCache getCache() {
        return cache;
    }

    public <T> T getInstanceOfType(Class<T> type) {
        Set<Component> components = cache.getComponents();
        Optional<Component> optionalComponent = components.stream().filter(c -> c.getType().equals(type)).findFirst();
        if(optionalComponent.isEmpty()) throw new NoSuchComponentException(type);
        Object result = optionalComponent.get().getInstance();
        if(type.isInstance(result)) return type.cast(result);
        throw new ComponentCastError("Unable to cast component from " + result.getClass() + " to " + type);
    }

    public ReflectionContext getReflectionContext() {
        return reflectionContext;
    }
}
