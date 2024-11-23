package framework.context;

import org.reflections.Reflections;

import java.time.Instant;

public class QuickLinkContext {
    private final ResultCache cache;
    private final ReflectionContext reflectionContext;
    private Instant lastTime;

    public QuickLinkContext(Class<?> root) {
        String packageName = root.getPackage().getName();
        this.lastTime = Instant.now();
        cache = new ResultCache();
        reflectionContext = new ReflectionContext();
        Reflections reflections = new Reflections(packageName);
        reflectionContext.setProjectReflections(reflections);
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

    public ReflectionContext getReflectionContext() {
        return reflectionContext;
    }
}
