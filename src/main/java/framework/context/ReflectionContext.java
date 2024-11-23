package framework.context;

import framework.annotations.Injectable;
import org.reflections.Reflections;

public class ReflectionContext {
    private final Reflections annotationReflections = new Reflections(Injectable.class.getPackage().getName());
    private Reflections projectReflections = null;

    ReflectionContext() {}

    public Reflections getProjectReflections() {
        if (projectReflections == null) {
            throw new NullPointerException("projectReflections is not initialized");
        }
        return projectReflections;
    }

    public Reflections getAnnotationReflections() {
        return annotationReflections;
    }

    public void setProjectReflections(Reflections reflections) {
        projectReflections = reflections;
    }

}
