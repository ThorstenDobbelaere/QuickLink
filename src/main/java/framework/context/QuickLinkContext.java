package framework.context;

import framework.context.configurable.HttpConfiguration;
import framework.context.configurable.LogFormatter;
import framework.context.configurable.QuickLinkContextConfiguration;
import framework.exceptions.NoSuchComponentException;
import framework.exceptions.internal.ComponentCastError;
import framework.resolver.model.Component;
import org.reflections.Reflections;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public class QuickLinkContext {
    private final ResultCache cache;
    private final ReflectionContext reflectionContext;
    private Instant lastTime;
    private LogFormatter logFormatter = new LogFormatter();
    private HttpConfiguration httpConfiguration = new HttpConfiguration();

    public QuickLinkContext(Class<?> root) {
        String packageName = root.getPackage().getName();
        this.lastTime = Instant.now();
        cache = new ResultCache();
        reflectionContext = new ReflectionContext();
        Reflections reflections = new Reflections(packageName);
        reflectionContext.setProjectReflections(reflections);
    }

    public QuickLinkContext(Class<?> root, QuickLinkContextConfiguration config) {
        this(root);

        LogFormatter logFormatter = config.getLogFormatter();
        if(logFormatter != null)
            this.logFormatter = logFormatter;

        HttpConfiguration httpConfiguration = config.getHttpConfiguration();
        if(httpConfiguration != null)
            this.httpConfiguration = httpConfiguration;
    }

    public LogFormatter getLogFormatter() {
        return logFormatter;
    }

    public HttpConfiguration getHttpConfiguration() {
        return httpConfiguration;
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
