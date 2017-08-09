package simpleNEAT.NeuralNetwork;

import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NeuralNetworkTest {

    private Node _node1, _node2, _node3;
    private Connection _connection1_2, _connection2_3;
    private final ArrayList<Node> _nodes;
    private final LinkedList<Connection> _connectionsSorted;
    private final NeuralNetwork _network;

    NeuralNetworkTest() {
        _node1 = new Node(12, -4, -9, false);
        _node2 = new Node(1, 2, 4, true);
        _node3 = new Node(7, -4, 1, false);
        _connection1_2 = new Connection(5, 12, 1, 0.6, false);
        _connection2_3 = new Connection(127684, 1, 7, 80, false);

        _node1.markConnectedInto(1);
        _node2.markConnectedInto(7);

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
    void constructsInstanceCorrectly() {
        assertEquals(_nodes, _network.getNodes());
        assertEquals(_connectionsSorted, _network.getConnectionsSorted());
    }

    @Test
    void addsNodesAtTheEnd() {
        Node node4 = new Node(9, 23.1, 4, true);
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
    void marksRelevantNodesAsConnected() {
        Connection connection1_3 = new Connection(2, 12, 7, 12.7, false);
        Connection connection2_2 = new Connection(130000, 1, 1, -8.0, false);

        _network.addConnection(connection1_3);
        _network.addConnection(connection2_2);

        assertTrue(_node1.isConnectedInto(7));
        assertTrue(_node2.isConnectedInto(1));
    }
}