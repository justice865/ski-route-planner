package edu.kit.kastel.model.graph.core;

import edu.kit.kastel.model.graph.types.NodeConnectionStatus;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The class that represents a node in the ski area graph.
 * @author uqibg
 */
public abstract class Node {
    private final String id;
    private final Set<Node> outgoing;

    /**
     * Constructor for the Node class.
     * @param id the unique identifier for the node
     */
    public Node(String id) {
        this.id = id;
        this.outgoing = new LinkedHashSet<>();
    }

    /**
     * A getter for the id of the node.
     * @return the id of the node
     */
    public String getId() {
        return this.id;
    }

    /**
     * A getter for the outgoing connections of the node.
     * @return a list of the outgoing connections of the node
     */
    public List<Node> getOutgoing() {
        return List.copyOf(this.outgoing);
    }

    /**
     * Checks if there is an outgoing connection from this node to the target node.
     * @param target the target node to check the connection to
     * @return true if there is an outgoing connection to the target node, false otherwise
     */
    public boolean hasOutgoingTo(Node target) {
        return target != null && this.outgoing.contains(target);
    }


    /**
     * Connects this node to the target node and returns the status of the connection attempt.
     * @param target the target node to connect to
     * @return the status of the connection attempt
     */
    public NodeConnectionStatus connectToWithStatus(Node target) {
        if (target == null) {
            return NodeConnectionStatus.TARGET_NULL;
        }
        if (this == target) {
            return NodeConnectionStatus.SELF_CONNECTION;
        }
        if (this.outgoing.contains(target)) {
            return NodeConnectionStatus.DUPLICATE_CONNECTION;
        }

        boolean reverseExists = target.hasOutgoingTo(this);
        boolean sameType = this.getClass().equals(target.getClass());
        if (reverseExists && sameType) {
            return NodeConnectionStatus.REVERSE_SAME_TYPE_NOT_ALLOWED;
        }

        this.outgoing.add(target);
        return NodeConnectionStatus.SUCCESS;
    }
}

