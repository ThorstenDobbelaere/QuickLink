package framework.context;

import org.reflections.Reflections;

public class ReflectionContext {
    private Reflections projectReflections = null;

    ReflectionContext() {}

    public Reflections getProjectReflections() {
        if (projectReflections == null) {
            throw new NullPointerException("projectReflections is not initialized");
        }
        return projectReflections;
    }

    public void setProjectReflections(Reflections reflections) {
        projectReflections = reflections;
    }

}
