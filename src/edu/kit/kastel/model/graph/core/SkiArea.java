package edu.kit.kastel.model.graph.core;

import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.elements.Slope;
import edu.kit.kastel.model.graph.types.NodeConnectionStatus;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The SkiArea class represents a ski area graph with its nodes amd connections.
 * @author uqibg
 */
public class SkiArea {
    private final Map<String, Node> nodesById;
    private final Set<Node> nodes;
    private boolean duplicateIdsDetected;

    /**
     * Constructor for SkiArea.
     */
    public SkiArea() {
        this.nodesById = new LinkedHashMap<>();
        this.nodes = new HashSet<>();
        this.duplicateIdsDetected = false;
    }

    /**
     * Adds a node to the ski area.
     * @param node the node to be added
     */
    public void addNode(Node node) {
        if (node == null) {
            return;
        }

        String id = node.getId();
        if (this.nodesById.containsKey(id)) {
            this.duplicateIdsDetected = true;
            return;
        }
        this.nodesById.put(id, node);
        this.nodes.add(node);
    }

    /**
     * Adds a connection from one node to another.
     * @param from the node from which the connection starts
     * @param to the node to which the connection goes
     * @return the status of the connection attempt
     */
    public NodeConnectionStatus addConnection(Node from, Node to) {
        if (from == null || to == null) {
            return NodeConnectionStatus.SUCCESS;
        }
        if (!this.nodes.contains(from) || !this.nodes.contains(to)) {
            return NodeConnectionStatus.SUCCESS;
        }
        return from.connectToWithStatus(to);
    }


    /**
     * Retrieves the node with the given id from the ski area.
     * @param id the id of the node to retrieve
     * @return the node with the given id, or null if no such node exists
     */
    public Node getNode(String id) {
        return id != null ? this.nodesById.get(id) : null;
    }

    /**
     * Retrieves the outgoing connections from a given node.
     * @param node the node for which to retrieve the outgoing connections
     * @return a list of nodes that are directly reachable from the given node
     */
    public List<Node> getOutgoing(Node node) {
        if (node == null || !this.nodes.contains(node)) {
            return List.of();
        }
        return node.getOutgoing();
    }

    /**
     * Retrieves a list of all lifts in the ski area.
     * @return a list of all lifts in the ski area
     */
    public List<Lift> getLifts() {
        List<Lift> lifts = new LinkedList<>();
        for (Node node : this.nodesById.values()) {
            if (node instanceof Lift lift) {
                lifts.add(lift);
            }
        }
        return lifts;
    }

    /**
     * Retrieves a list of all slopes in the ski area.
     * @return a list of all slopes in the ski area
     */
    public List<Slope> getSlopes() {
        List<Slope> slopes = new LinkedList<>();
        for (Node node : this.nodesById.values()) {
            if (node instanceof Slope slope) {
                slopes.add(slope);
            }
        }
        return slopes;
    }

    /**
     * Retrieves a list of all transit lifts in the ski area.
     * @return a list of all transit lifts in the ski area
     */
    public List<Lift> getTransitLifts() {
        List<Lift> transitLifts = new LinkedList<>();
        for (Node node : this.nodesById.values()) {
            if (node instanceof Lift lift && lift.isTransit()) {
                transitLifts.add(lift);
            }
        }
        return transitLifts;
    }

    /**
     * Checks if the ski area is valid.
     * @return true if the ski area is valid, false otherwise
     */
    public boolean isValid() {
        return hasUniqueIds()
                && hasAtLeastOneTransitLift()
                && hasAtLeastOneSlope()
                && isConnected();
    }

    /**
     * Checks if all nodes in the ski area have unique ids.
     * @return true if all nodes have unique ids, false if duplicate ids were detected
     */
    public boolean hasUniqueIds() {
        return !this.duplicateIdsDetected;
    }

    /**
     * Checks if the ski area has at least one transit lift.
     * @return true if the ski area has at least one transit lift, false otherwise
     */
    public boolean hasAtLeastOneTransitLift() {
        return !getTransitLifts().isEmpty();
    }

    /**
     * Checks if the ski area has at least one slope.
     * @return true if the ski area has at least one slope, false otherwise
     */
    public boolean hasAtLeastOneSlope() {
        return !getSlopes().isEmpty();
    }

    /**
     * Checks if the ski area is connected, meaning that there is a path between any two nodes in the ski area.
     * @return true if the ski area is connected, false otherwise
     */
    public boolean isConnected() {
        if (this.nodesById.isEmpty()) {
            return false;
        }

        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        Node start = this.nodesById.values().iterator().next();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node next : current.getOutgoing()) {
                if (this.nodes.contains(next) && visited.add(next)) {
                    queue.add(next);
                }
            }

            for (Node candidate : this.nodesById.values()) {
                if (candidate.hasOutgoingTo(current) && visited.add(candidate)) {
                    queue.add(candidate);
                }
            }
        }

        return visited.size() == this.nodesById.size();
    }
}

