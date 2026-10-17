package edu.kit.kastel.model.graph.types;

/**
 * This enum represents the outcome of attempting to connect two nodes of a ski area graph.
 * @author uqibg
 */
public enum NodeConnectionStatus {
    /**
     * Indicates that the connection between the two nodes was successful.
     */
    SUCCESS,
    /**
     * Indicates that the connection failed because the target node is null.
     */
    TARGET_NULL,
    /**
     * Indicates that the connection failed because the source and target nodes are the same.
     */
    SELF_CONNECTION,
    /**
     * Indicates that the connection failed because there is already the same connection between the source and target nodes.
     */
    DUPLICATE_CONNECTION,
    /**
     * Indicates that the two nodes are the same type and only one direction of connection is allowed between them.
     */
    REVERSE_SAME_TYPE_NOT_ALLOWED
}


