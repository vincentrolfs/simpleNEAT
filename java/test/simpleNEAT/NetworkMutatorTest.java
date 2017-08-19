package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMutatorTest {

    private NetworkCreator _networkCreator;
    private Connection _initialConnection_1;
    private Node _inputNode_1;
    private Node _outputNode_1;
    private NeuralNetwork _network_1;

    private NetworkMutator _mutatorForForcedAddConnectionMutation;
    private NetworkMutator _mutatorForForcedAddNodeMutation;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(
            1, 1,
            -9, 2, 1,
            -1, 2, 0.8,
            0.3, 1.3, 1.88,
            1
        );

        _network_1 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> initialNodes = _network_1.getNodes();
        _inputNode_1 = initialNodes.get(0);
        _outputNode_1 = initialNodes.get(1);

        _initialConnection_1 = _networkCreator.createConnectionWithRandomWeight(
                _inputNode_1.getInnovationNumber(),
                _outputNode_1.getInnovationNumber()
        );
        _initialConnection_1.setWeight(1.2);
        _network_1.addConnection(_initialConnection_1);

        _mutatorForForcedAddConnectionMutation = new NetworkMutator(_networkCreator,
                0, 0, 0, 1,
                1000, false,
                0, 0, 0);

        _mutatorForForcedAddNodeMutation = new NetworkMutator(_networkCreator,
                0, 0, 1, 0,
                1000, false,
                0, 0, 0);
    }

    @Test
    void validateSetUp() {
        ArrayList<Node> nodesExpected = new ArrayList<Node> (){{
           add(_inputNode_1);
           add(_outputNode_1);
        }};
        LinkedList<Connection> connectionsExpected = new LinkedList<Connection> (){{
           add(_initialConnection_1);
        }};

        assertEquals(nodesExpected, _network_1.getNodes());
        assertEquals(connectionsExpected, _network_1.getConnectionsSorted());
        assertEquals(1, _network_1.getAmountInputNodes());
        assertEquals(1, _network_1.getAmountOutputNodes());
    }

    @Test
    void forcedAddNodeMutationResultsInThreeNodes(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes = _network_1.getNodes();

        assertEquals(3, nodes.size());
    }

    @Test
    void forcedAddNodeMutationInsertsNewNodeCorrectly(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes = _network_1.getNodes();

        assertEquals(_inputNode_1, nodes.get(0));
        assertEquals(_outputNode_1, nodes.get(1));
        assertNotEquals(_inputNode_1, nodes.get(2));
        assertNotEquals(_outputNode_1, nodes.get(2));
    }

    @Test
    void forcedAddNodeMutationResultsInThreeConnections(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Connection> connections = _network_1.getConnectionsSorted();

        assertEquals(3, connections.size());
    }

    @Test
    void forcedAddNodeMutationDisablesOldConnection(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        assertTrue(_initialConnection_1.isDisabled());
    }

    @Test
    void forcedAddNodeMutationInsertsNewConnectionsCorrectly(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes= _network_1.getNodes();
        List<Connection> connectionsSorted = _network_1.getConnectionsSorted();
        Node newNode = nodes.get(2);
        Connection newConnectionIn = connectionsSorted.get(1);
        Connection newConnectionOut = connectionsSorted.get(2);

        assertEquals(_initialConnection_1, connectionsSorted.get(0));

        assertEquals(_inputNode_1.getInnovationNumber(), newConnectionIn.getNodeOutOfId());
        assertEquals(newNode.getInnovationNumber(), newConnectionIn.getNodeIntoId());

        assertEquals(newNode.getInnovationNumber(), newConnectionOut.getNodeOutOfId());
        assertEquals(_outputNode_1.getInnovationNumber(), newConnectionOut.getNodeIntoId());
    }

    @Test
    void forcedAddNodeMutationCreatesNewConnectionsWithCorrectWeights(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Connection> connectionsSorted = _network_1.getConnectionsSorted();
        Connection newConnectionIn = connectionsSorted.get(1);
        Connection newConnectionOut = connectionsSorted.get(2);

        assertEquals(1, newConnectionIn.getWeight());
        assertEquals(1.2, newConnectionOut.getWeight());
    }

    @Test
    void forcedAddNodeMutationCreatesNewConnectionsNonDisabled(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Connection> connectionsSorted = _network_1.getConnectionsSorted();
        Connection newConnectionIn = connectionsSorted.get(1);
        Connection newConnectionOut = connectionsSorted.get(2);

        assertFalse(newConnectionIn.isDisabled());
        assertFalse(newConnectionOut.isDisabled());
    }

    @Test
    void forcedAddNodeMutationCreatesNewNodeWithCorrectAttributes() {
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes = _network_1.getNodes();
        Node newNode = nodes.get(2);

        assertEquals(1.88, newNode.getActivationSteepness());
        assertEquals(0.8, newNode.getBias());
        assertFalse(newNode.isDisabled());
    }

    @Test
    void forcedAddConnectionMutationResultsInTwoConnections(){
        _mutatorForForcedAddConnectionMutation.mutate(_network_1);
        List<Connection> connections = _network_1.getConnectionsSorted();

        assertEquals(2, connections.size());
    }

    @Test
    void forcedAddConnectionMutationIsInsertedCorrectly() {
        _mutatorForForcedAddConnectionMutation.mutate(_network_1);
        List<Connection> connections = _network_1.getConnectionsSorted();
        Connection newConnection = connections.get(1);

        assertEquals(_initialConnection_1, connections.get(0));
        assertNotEquals(_initialConnection_1, newConnection);
        assertTrue(_initialConnection_1.getInnovationNumber() < newConnection.getInnovationNumber());
    }

    @Test
    void forcedAddConnectionMutationIsChosenUniformlyFromAllPossibilities() {
        int input_input_chosen = 0;
        int output_output_chosen = 0;
        int output_input_chosen = 0;

        for (int i = 1; i < 45000; i++) {
            setUp();

            _mutatorForForcedAddConnectionMutation.mutate(_network_1);
            List<Connection> connections = _network_1.getConnectionsSorted();
            Connection newConnection = connections.get(1);

            int nodeOutOfId = newConnection.getNodeOutOfId();
            int nodeIntoId = newConnection.getNodeIntoId();
            int inputNodeId = _inputNode_1.getInnovationNumber();
            int outputNodeId = _outputNode_1.getInnovationNumber();

            if (nodeOutOfId == inputNodeId && nodeIntoId == inputNodeId){
                input_input_chosen++;
            } else if (nodeOutOfId == outputNodeId && nodeIntoId == outputNodeId){
                output_output_chosen++;
            } else if (nodeOutOfId == outputNodeId && nodeIntoId == inputNodeId){
                output_input_chosen++;
            } else {
                assert false;
            }
        }

        // 99,7%-confidence interval
        assertEquals(15000, input_input_chosen, 300);
        assertEquals(15000, output_output_chosen, 300);
        assertEquals(15000, output_input_chosen, 300);
    }

    @RepeatedTest(10)
    void forcedAddConnectionMutationIsHasCorrectWeights() {
        _mutatorForForcedAddConnectionMutation.mutate(_network_1);
        List<Connection> connections = _network_1.getConnectionsSorted();
        Connection newConnection = connections.get(1);

        assertTrue(newConnection.getWeight() >= -9);
        assertTrue(newConnection.getWeight() <= 2);
    }

}