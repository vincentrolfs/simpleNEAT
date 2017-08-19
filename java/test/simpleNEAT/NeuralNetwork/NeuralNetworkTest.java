package simpleNEAT.NeuralNetwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NeuralNetworkTest {

    private Node _node1, _node2, _node3;
    private Connection _connection1_2, _connection2_3;
    private ArrayList<Node> _nodes;
    private LinkedList<Connection> _connectionsSorted;
    private NeuralNetwork _network;

    @BeforeEach
    void setUp() {
        _node1 = new Node(12, -4, -9);
        _node2 = new Node(1, 2, 4);
        _node3 = new Node(7, -4, 1);
        _connection1_2 = new Connection(5, 12, 1, 0.6, false);
        _connection2_3 = new Connection(127684, 1, 7, 80, false);

        _nodes = new ArrayList<Node>(){{
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
        assertEquals(_nodes, _network.getNodes());
    }

    @Test
    void constructsConnectionsCorrectly() {
        assertEquals(_connectionsSorted, _network.getConnectionsSorted());
    }

    @Test
    void hasConnectionBetweenWorksCorrectlyAfterConstruction() {
        assertTrue(_network.hasConnectionBetween(1, 7));
        assertTrue(_network.hasConnectionBetween(12, 1));

        assertFalse(_network.hasConnectionBetween(1, 1));
        assertFalse(_network.hasConnectionBetween(1, 12));
        assertFalse(_network.hasConnectionBetween(7, 1));
        assertFalse(_network.hasConnectionBetween(7, 12));
        assertFalse(_network.hasConnectionBetween(7, 7));
        assertFalse(_network.hasConnectionBetween(12, 7));
        assertFalse(_network.hasConnectionBetween(12, 12));
    }

    @Test
    void nodeIdInNetworkWorksCorrectlyAfterConstruction() {
        assertTrue(_network.isNodeIdInNetwork(1));
        assertTrue(_network.isNodeIdInNetwork(12));
        assertTrue(_network.isNodeIdInNetwork(7));
        assertFalse(_network.isNodeIdInNetwork(0));
        assertFalse(_network.isNodeIdInNetwork(5));
        assertFalse(_network.isNodeIdInNetwork(127684));
    }

    @Test
    void addsNodesAtTheEnd() {
        Node node4 = new Node(9, 23.1, 4);
        _network.addNode(node4);

        List<Node> expected = new ArrayList<Node> (){{
            add(_node1);
            add(_node2);
            add(_node3);
            add(node4);
        }};

        assertEquals(expected, _network.getNodes());
    }

    @Test
    void registersNodeId() {
        Node node4 = new Node(9, 23.1, 4);
        _network.addNode(node4);
        assertTrue(_network.isNodeIdInNetwork(9));
    }

    @Test
    void addsConnectionsAtCorrectPosition() {
        Connection connection1_3 = new Connection(2, 12, 7, 12.7, false);
        Connection connection3_1 = new Connection(214, 7, 12, -1.1, false);
        Connection connection2_2 = new Connection(130000, 1, 1, -8.0, false);
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
    void hasConnectionsBetweenTrueAfterAddingConnection() {
        Connection connection1_3 = new Connection(2, 12, 7, 12.7, false);
        Connection connection2_2 = new Connection(130000, 1, 1, -8.0, false);

        _network.addConnection(connection1_3);
        _network.addConnection(connection2_2);

        assertTrue(_network.hasConnectionBetween(12, 7));
        assertTrue(_network.hasConnectionBetween(1, 1));
    }

    @Test
    void insertionOfConnectionIntoConnectionlessNetworkWorks() {
        Node node1 = new Node(0, 0.5, 0.9);
        Node node2 = new Node(1, -3, -3.3);
        Node node3 = new Node(2, -1, -2.7);
        ArrayList<Node> nodes = new ArrayList<Node>() {{
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