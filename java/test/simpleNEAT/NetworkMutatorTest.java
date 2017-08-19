package simpleNEAT;

import org.jcp.xml.dsig.internal.MacOutputStream;
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

    private Connection _initialConnection_i_o_2;
    private Connection _initialConnection_o_o_2;
    private Node _inputNode_2;
    private Node _outputNode_2;
    private NeuralNetwork _network_2;

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

        initializeNetwork1();
        initializeNetwork2();
        initializeMutators();
    }

    private void initializeNetwork1() {
        _network_1 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> initialNodes = _network_1.getNodes();
        _inputNode_1 = initialNodes.get(0);
        _outputNode_1 = initialNodes.get(1);

        _initialConnection_1 = _networkCreator.createConnectionWithGivenWeight(
                _inputNode_1.getInnovationNumber(),
                _outputNode_1.getInnovationNumber(),
                1.2
        );
        _network_1.addConnection(_initialConnection_1);
    }

    private void initializeNetwork2() {
        _network_2 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> initialNodes = _network_2.getNodes();
        _inputNode_2 = initialNodes.get(0);
        _outputNode_2 = initialNodes.get(1);

        _initialConnection_i_o_2 = _networkCreator.createConnectionWithGivenWeight(
                _inputNode_2.getInnovationNumber(),
                _outputNode_2.getInnovationNumber(),
                -0.5
        );
        _initialConnection_o_o_2 = _networkCreator.createConnectionWithGivenWeight(
                _outputNode_2.getInnovationNumber(),
                _outputNode_2.getInnovationNumber(),
                1.8
        );
        _initialConnection_o_o_2.setDisabled(true);
        _network_2.addConnection(_initialConnection_i_o_2);
        _network_2.addConnection(_initialConnection_o_o_2);
    }

    private void initializeMutators() {
        _mutatorForForcedAddConnectionMutation = new NetworkMutator(
                _networkCreator,
                0,
                0,
                0,
                0,
                1,
                0,
                0,
                0,
                1,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );

        _mutatorForForcedAddNodeMutation = new NetworkMutator(
                _networkCreator,
                0,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
    }

    @Test
    void validateSetUp() {
        validateNetwork1();
        validateNetwork2();
    }

    private void validateNetwork1() {
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

    private void validateNetwork2() {
        ArrayList<Node> nodesExpected = new ArrayList<Node> (){{
            add(_inputNode_2);
            add(_outputNode_2);
        }};
        LinkedList<Connection> connectionsExpected = new LinkedList<Connection> (){{
            add(_initialConnection_i_o_2);
            add(_initialConnection_o_o_2);
        }};

        assertEquals(nodesExpected, _network_2.getNodes());
        assertEquals(connectionsExpected, _network_2.getConnectionsSorted());

        assertEquals(1, _network_2.getAmountInputNodes());
        assertEquals(1, _network_2.getAmountOutputNodes());

        assertEquals(-0.5, _initialConnection_i_o_2.getWeight());
        assertEquals(1.8, _initialConnection_o_o_2.getWeight());
        assertFalse(_initialConnection_i_o_2.isDisabled());
        assertTrue(_initialConnection_o_o_2.isDisabled());
    }

    @Test
    void forcedConnectionDisabledMutationDisablesConnections(){
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                1,
                0,
                0,
                0,
                0,
                1,
                0,
                0,
                1,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        mutator.mutate(_network_2);
        List<Connection> connections = _network_2.getConnectionsSorted();
        LinkedList<Connection> connectionsExpected = new LinkedList<Connection> (){{
            add(_initialConnection_i_o_2);
            add(_initialConnection_o_o_2);
        }};

        assertEquals(connectionsExpected, connections);

        assertTrue(_initialConnection_i_o_2.isDisabled());
        assertFalse(_initialConnection_o_o_2.isDisabled());
    }

    @Test
    void forcedConnectionDisabledMutationOnlySometimesAffectsConnections(){
        int amountConnection1Affected = 0;
        int amountConnection2Affected = 0;

        for (int i = 0; i < 1000; i++) {
            setUp();
            NetworkMutator mutator = new NetworkMutator(
                    _networkCreator,
                    1,
                    0,
                    0,
                    0,
                    0,
                    0.5,
                    0,
                    0,
                    1,
                    0,
                    0,
                    0,
                    1,
                    1,
                    1000,
                    false
            );
            mutator.mutate(_network_2);

            if (_initialConnection_i_o_2.isDisabled()){
                amountConnection1Affected++;
            }
            if (!_initialConnection_o_o_2.isDisabled()){
                amountConnection2Affected++;
            }

        }

        assertTrue(amountConnection1Affected > 0);
        assertTrue(amountConnection1Affected < 1000);
        assertTrue(amountConnection2Affected > 0);
        assertTrue(amountConnection2Affected < 1000);
    }

    @Test
    void forcedConnectionWeightMutationChangesConnectionWeights() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                0.6,
                10,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        mutator.mutate(_network_2);

        assertNotEquals(-0.5, _initialConnection_i_o_2.getWeight());
        assertNotEquals(1.8, _initialConnection_o_o_2.getWeight());
    }

    @Test
    void forcedConnectionWeightMutationRespectsMaxPerturbationMagnitude() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                0,
                0.01,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        mutator.mutate(_network_2);

        double weight1 = _initialConnection_i_o_2.getWeight();
        double weight2 = _initialConnection_o_o_2.getWeight();
        double absoluteDifference1 = Math.abs(-0.5 - weight1);
        double absoluteDifference2 = Math.abs(1.8 - weight2);

        assertTrue(absoluteDifference1 > 0);
        assertTrue(absoluteDifference2 > 0);
        assertTrue(absoluteDifference1 <= 0.01);
        assertTrue(absoluteDifference2 <= 0.01);
    }

    @Test
    void forcedConnectionWeightMutationRespectsWeightLimits() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                0,
                10000000,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        mutator.mutate(_network_2);

        double weight1 = _initialConnection_i_o_2.getWeight();
        double weight2 = _initialConnection_o_o_2.getWeight();

        assertTrue(weight1 <= 2);
        assertTrue(weight2 <= 2);
        assertTrue(weight1 >= -9);
        assertTrue(weight2 >= -9);
    }

    @Test
    void forcedAddNodeMutationDoesNothingOnConnectionlessNetwork() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        _mutatorForForcedAddNodeMutation.mutate(network);

        List<Node> nodes = network.getNodes();
        List<Connection> connectionsSorted = network.getConnectionsSorted();

        assertEquals(2, nodes.size());
        assertEquals(0, connectionsSorted.size());
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
    }

    @Test
    void fullyConnectedNetworkMakesAddConnectionMutationFallBackToConnectionWeightMutation() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        int inputNodeId = network.getNodes().get(0).getInnovationNumber();
        int outputNodeId = network.getNodes().get(1).getInnovationNumber();

        Connection connection1 = _networkCreator.createConnectionWithGivenWeight(inputNodeId, inputNodeId, -2);
        Connection connection2 = _networkCreator.createConnectionWithGivenWeight(outputNodeId, outputNodeId, -2);
        Connection connection3 = _networkCreator.createConnectionWithGivenWeight(inputNodeId, outputNodeId, -2);
        Connection connection4 = _networkCreator.createConnectionWithGivenWeight(outputNodeId, inputNodeId, -2);

        network.addConnection(connection1);
        network.addConnection(connection2);
        network.addConnection(connection3);
        network.addConnection(connection4);

        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                0,
                0,
                0,
                1,
                0,
                1,
                0.4,
                20,
                0,
                0,
                0,
                1,
                1,
                10,
                true
        );

        mutator.mutate(network);
        assertNotEquals(-2, connection1.getWeight());
        assertNotEquals(-2, connection2.getWeight());
        assertNotEquals(-2, connection3.getWeight());
        assertNotEquals(-2, connection4.getWeight());
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
    void forcedAddConnectionMutationProducesConnectionWithCorrectWeights() {
        _mutatorForForcedAddConnectionMutation.mutate(_network_1);
        List<Connection> connections = _network_1.getConnectionsSorted();
        Connection newConnection = connections.get(1);

        assertTrue(newConnection.getWeight() >= -9);
        assertTrue(newConnection.getWeight() <= 2);
    }

}