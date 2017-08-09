package simpleNEAT.NeuralNetwork;

import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Node;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void marksConnectionsCorrectly() {
        Node node1 = new Node(213, -1.3, 0.9, false);
        Node node2 = new Node(20, 23.1, 4, true);

        node1.markConnectedInto(103);
        assertTrue(node1.isConnectedInto(103));

        node2.markConnectedInto(20);
        assertTrue(node2.isConnectedInto(20));
    }

    @Test
    void isNotConnectedByDefault() {
        Node node1 = new Node(12, -4, -9, false);

        assertFalse(node1.isConnectedInto(12));
        assertFalse(node1.isConnectedInto(0));
        assertFalse(node1.isConnectedInto(-2));
        assertFalse(node1.isConnectedInto(11));
        assertFalse(node1.isConnectedInto(11100));
    }
}