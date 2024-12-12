package framework.setup;

import framework.context.QuickLinkContext;
import framework.exceptions.scanning.CyclicalDependencyException;
import framework.setup.model.Component;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;

public class GraphChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphChecker.class);

    public static void checkCycles(QuickLinkContext context) {
        Graph<Class<?>, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        var cache = context.getCache();
        Set<Component> components = cache.getComponents();

        components.forEach(component -> graph.addVertex(component.getType()));

        components.forEach(component -> Arrays.stream(component.getDependencies()).forEach(dependency -> {
            graph.addEdge(component.getType(), dependency);
        }));

        var cycleDetector = new CycleDetector<>(graph);
        if(cycleDetector.detectCycles()){
            throw new CyclicalDependencyException(cycleDetector.findCycles());
        }

        LOGGER.info("No cyclical dependencies found.");

    }

}
