package de.svenojo.catan.examples;

import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import de.svenojo.catan.world.Node;

public class GraphExample {
    
    public void Example1() {
        Graph<String, DefaultEdge> g = GraphTypeBuilder
            .<String, DefaultEdge> undirected().allowingMultipleEdges(false)
            .allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
        String google = "http://www.google.com";
        String wikipedia = "http://www.wikipedia.org";
        String jgrapht = "http://www.jgrapht.org";
        String moin = "moin";

        // add the vertices
        g.addVertex(google);
        g.addVertex(wikipedia);
        g.addVertex(jgrapht);
        g.addVertex(moin);

        // add edges to create linking structure
        g.addEdge(jgrapht, wikipedia);
        g.addEdge(google, jgrapht);
        g.addEdge(google, wikipedia);
        g.addEdge(wikipedia, moin);

        Set<DefaultEdge> edges = g.edgesOf(jgrapht);
        
        for (DefaultEdge edge : edges) {
            String source = g.getEdgeSource(edge);
            String target = g.getEdgeTarget(edge);
            String neighbor = source.equals(jgrapht) ? target : source;
            System.out.println(neighbor);
        }
    }

    public void exportGraph() {
        DOTExporter<Node, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider((v) -> Map.of("label", DefaultAttribute.createAttribute(v.getNumber())));

        StringWriter writer = new StringWriter();
        //exporter.exportGraph(graph, writer);
    }
}
