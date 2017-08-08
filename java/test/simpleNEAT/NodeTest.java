package simpleNEAT;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void marksConnectionsCorrectly() {
        Node node1 = new Node(213, -1.3, 0.9, false);
        Node node2 = new Node(20, 23.1, 4, true);

        node1.markConnectedInto(node2);
        assertTrue(node1.isConnectedInto(node2));
    }

    @Test
    void isNotConnectedByDefault() {
        Node node1 = new Node(213, -1.3, 0.9, false);
        Node node2 = new Node(20, 23.1, 4, true);

        assertFalse(node1.isConnectedInto(node2));
        assertFalse(node2.isConnectedInto(node1));
        assertFalse(node1.isConnectedInto(node1));
        assertFalse(node2.isConnectedInto(node2));
    }
}