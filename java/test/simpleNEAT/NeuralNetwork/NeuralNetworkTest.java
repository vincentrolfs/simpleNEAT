package simpleNEAT.NeuralNetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NeuralNetworkTest {

    private Node _node1, _node2, _node3;
    private Connection _connection1_2, _connection2_3;
    private LinkedList<Node> _nodes;
    private LinkedList<Connection> _connectionsSorted;
    private NeuralNetwork _network;

    @BeforeEach
    void setUp() {
        _node1 = new Node(5, -4, -9);
        _node2 = new Node(18, 2, 4);
        _node3 = new Node(99, -4, 1);
        _connection1_2 = new Connection(5, 99, 5, 0.6, false);
        _connection2_3 = new Connection(127684, 5, 18, 80, false);

        _nodes = new LinkedList<Node>(){{
            add(_node1);
            add(_node2);
            add(_node3);
        }};
        _connectionsSorted = new LinkedList<Connection>(){{
            add(_connection1_2);
            add(_connection2_3);
        }};
        _network = new NeuralNetwork(_nodes, _connectionsSorted, 1, 1);
    }

    @Test
    void constructsNodesCorrectly() {
        assertEquals(_nodes, _network.getNodesSorted());
    }

    @Test
    void constructsConnectionsCorrectly() {
        assertEquals(_connectionsSorted, _network.getConnectionsSorted());
    }

    @Test
    void hasConnectionBetweenWorksCorrectlyAfterConstruction() {
        assertTrue(_network.hasConnectionBetween(99, 5));
        assertTrue(_network.hasConnectionBetween(5, 18));

        assertFalse(_network.hasConnectionBetween(5, 5));
        assertFalse(_network.hasConnectionBetween(5, 99));
        assertFalse(_network.hasConnectionBetween(18, 5));
        assertFalse(_network.hasConnectionBetween(18, 18));
        assertFalse(_network.hasConnectionBetween(18, 99));
        assertFalse(_network.hasConnectionBetween(99, 18));
        assertFalse(_network.hasConnectionBetween(99, 99));
    }

    @Test
    void nodeIdInNetworkWorksCorrectlyAfterConstruction() {
        assertTrue(_network.isNodeIdInNetwork(5));
        assertTrue(_network.isNodeIdInNetwork(18));
        assertTrue(_network.isNodeIdInNetwork(99));

        assertFalse(_network.isNodeIdInNetwork(0));
        assertFalse(_network.isNodeIdInNetwork(1));
        assertFalse(_network.isNodeIdInNetwork(98));
        assertFalse(_network.isNodeIdInNetwork(127684));
    }


    @Test
    void registersNodeId() {
        Node node4 = new Node(9, 23.1, 4);
        _network.addNode(node4);
        assertTrue(_network.isNodeIdInNetwork(9));
    }

    @Test
    void addsConnectionsAtCorrectPosition() {
        Connection connection1_3 = new Connection(2, 99, 18, 12.7, false);
        Connection connection3_1 = new Connection(214, 18, 99, -1.1, false);
        Connection connection2_2 = new Connection(130000, 5, 5, -8.0, false);
        LinkedList<Connection> expectedConnectionsSorted = new LinkedList<Connection>() {{
            add(connection1_3);
            add(_connection1_2);
            add(connection3_1);
            add(_connection2_3);
            add(connection2_2);
        }};

        _network.addConnection(connection3_1);
        _network.addConnection(connection1_3);
        _network.addConnection(connection2_2);

        assertEquals(expectedConnectionsSorted, _network.getConnectionsSorted());
    }

    @Test
    void addsNodesAtCorrectPosition() {
        Node node4 = new Node(1, -4, -9);
        Node node5 = new Node(7, 0, -99);
        Node node6 = new Node(19,  10000, 34);
        Node node7 = new Node(10000, -1, -3);
        Node node8 = new Node(15, 1, 3);
        LinkedList<Node> expectedNodesSorted = new LinkedList<Node>() {{
            add(node4);
            add(_node1);
            add(node5);
            add(node8);
            add(_node2);
            add(node6);
            add(_node3);
            add(node7);
        }};

        _network.addNode(node4);
        _network.addNode(node5);
        _network.addNode(node6);
        _network.addNode(node7);
        _network.addNode(node8);

        assertEquals(expectedNodesSorted, _network.getNodesSorted());
    }

    @Test
    void hasConnectionsBetweenTrueAfterAddingConnection() {
        Connection connection1_3 = new Connection(2, 99, 18, 12.7, false);
        Connection connection2_2 = new Connection(130000, 5, 5, -8.0, false);

        _network.addConnection(connection1_3);
        _network.addConnection(connection2_2);

        assertTrue(_network.hasConnectionBetween(99, 18));
        assertTrue(_network.hasConnectionBetween(5, 5));
    }

    @Test
    void insertionOfConnectionIntoConnectionlessNetworkWorks() {
        Node node1 = new Node(0, 0.5, 0.9);
        Node node2 = new Node(1, -3, -3.3);
        Node node3 = new Node(2, -1, -2.7);
        LinkedList<Node> nodes = new LinkedList<Node>() {{
            add(node1);
            add(node2);
            add(node3);
        }};
        LinkedList<Connection> connections = new LinkedList<>();
        NeuralNetwork network = new NeuralNetwork(nodes, connections, 1, 2);

        Connection newConnection = new Connection(70, 0, 2, 0.5, true);

        network.addConnection(newConnection);
        List<Connection> allConnections = network.getConnectionsSorted();

        assertEquals(1, allConnections.size());
        assertEquals(newConnection, allConnections.get(0));
        assertTrue(network.hasConnectionBetween(0, 2));
    }
}