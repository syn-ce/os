package main.java.graph.datastructure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public Graph() {

    }

    public void addNodes(Node[] nodes) {
        this.nodes.addAll(Arrays.asList(nodes));
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<Node> getNodes() {
        return nodes;
    }

    public Node getNodeByName(String name) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        throw new RuntimeException("Node with name " + name + " not in Graph.");
    }
}