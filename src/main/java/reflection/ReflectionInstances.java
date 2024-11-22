package reflection;

import org.reflections.Reflections;

public class ReflectionInstances {
    private static final Reflections annotationReflections = new Reflections("annotations");
    private static Reflections projectReflections = null;

    public static Reflections getProjectReflections() {
        if (projectReflections == null) {
            throw new NullPointerException("projectReflections is not initialized");
        }
        return projectReflections;
    }

    public static Reflections getAnnotationReflections() {
        return annotationReflections;
    }

    public static void setProjectReflections(Reflections reflections) {
        projectReflections = reflections;
    }

}
