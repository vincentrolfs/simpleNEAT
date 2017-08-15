package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.ArrayList;
import java.util.LinkedList;

class NetworkMutatorTest {

    private NetworkCreator _networkCreator;
    private Node _inputNode_1;
    private Node _outputNode_1;
    private Connection _initialConnection_1;
    private NeuralNetwork _network_1;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(
            1, 1,-9, 2,
            -1, 2, 0,
            0.3, 1.3, 1,
                1);
        _inputNode_1 = new Node(0, 0.8, 1, false);
        _outputNode_1 = new Node(1, 0.3, 1.2, false);
        _initialConnection_1 = new Connection(0, 0, 1, 2, false);

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(_inputNode_1);
        nodes.add(_outputNode_1);

        LinkedList<Connection> connectionsSorted = new LinkedList<>();
        connectionsSorted.add(_initialConnection_1);

        _network_1 = new NeuralNetwork(nodes, connectionsSorted, 1, 1);
    }
}