package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.LinkedList;
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
            0.3, 2, 1.88,
            1
        );

        initializeNetwork1();
        initializeNetwork2();
        initializeMutators();
    }

    private void initializeNetwork1() {
        _network_1 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> initialNodes = _network_1.getNodesSorted();
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
        List<Node> initialNodes = _network_2.getNodesSorted();
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
                0.8,
                0.99,
                0.9,
                0,
                1,
                1,
                1,
                0.7,
                4.4,
                1,
                0.5,
                0.9,
                3,
                3,
                1000,
                false
        );

        _mutatorForForcedAddNodeMutation = new NetworkMutator(
                _networkCreator,
                0.5,
                0,
                0,
                1,
                0,
                1,
                1,
                0.5,
                4,
                1,
                0.5,
                0.6,
                3,
                4,
                10,
                false
        );
    }

    @Test
    void validateSetUp() {
        validateNetwork1();
        validateNetwork2();
    }

    private void validateNetwork1() {
        LinkedList<Node> nodesExpected = new LinkedList<Node> (){{
            add(_inputNode_1);
            add(_outputNode_1);
        }};
        LinkedList<Connection> connectionsExpected = new LinkedList<Connection> (){{
            add(_initialConnection_1);
        }};

        assertEquals(nodesExpected, _network_1.getNodesSorted());
        assertEquals(connectionsExpected, _network_1.getConnectionsSorted());
        assertEquals(1, _network_1.getAmountInputNodes());
        assertEquals(1, _network_1.getAmountOutputNodes());
    }

    private void validateNetwork2() {
        LinkedList<Node> nodesExpected = new LinkedList<Node> (){{
            add(_inputNode_2);
            add(_outputNode_2);
        }};
        LinkedList<Connection> connectionsExpected = new LinkedList<Connection> (){{
            add(_initialConnection_i_o_2);
            add(_initialConnection_o_o_2);
        }};

        assertEquals(nodesExpected, _network_2.getNodesSorted());
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
                0.5,
                0.2,
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

        System.out.println(_network_2.getConnectionsSorted().size());
        mutator.mutate(_network_2);
        System.out.println(_network_2.getConnectionsSorted().size());

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
    void forcedConnectionWeightMutationGeneratesNewRandomWeight() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                1,
                0.005,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        mutator.mutate(_network_1);

        assertTrue(_initialConnection_1.getWeight() >= 1.21 || _initialConnection_1.getWeight() <= 1.19);
    }

    @Test
    void forcedConnectionWeightMutationAffectsWeightsAtTheCorrectProportion() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                1,
                0,
                0,
                0,
                0,
                0.382,
                0.5,
                3,
                0,
                0,
                0,
                1,
                1,
                1000,
                false
        );
        int amountWeightAffected = 0;
        for (int i = 0; i < 1000; i++) {
            _initialConnection_1.setWeight(0.2);
            mutator.mutate(_network_1);
            if (_initialConnection_1.getWeight() != 0.2){
                amountWeightAffected++;
            }
        }

        // 99,7% confidence interval
        assertEquals(382, amountWeightAffected, 48);
    }

    @Test
    void forcedNodeParameterMutationChangesNodeParameters() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                0.2,
                0.7,
                1.2,
                3.4,
                1000,
                false
        );
        assertEquals(0.8, _inputNode_1.getBias());
        assertEquals(1.88, _inputNode_1.getActivationSteepness());
        assertEquals(0.8, _outputNode_1.getBias());
        assertEquals(1.88, _outputNode_1.getActivationSteepness());

        mutator.mutate(_network_1);

        assertNotEquals(0.8, _inputNode_1.getBias());
        assertNotEquals(1.88, _inputNode_1.getActivationSteepness());
        assertNotEquals(0.8, _outputNode_1.getBias());
        assertNotEquals(1.88, _outputNode_1.getActivationSteepness());
    }

    @Test
    void forcedNodeParameterMutationRespectsMaxPerturbationMagnitude() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0.5,
                0.2,
                1,
                0.11,
                0.12,
                0,
                0,
                0,
                0,
                1,
                0,
                0,
                0.001,
                0.01,
                1000,
                false
        );
        double biasBefore = _inputNode_1.getBias();
        double activationSteepnessBefore = _inputNode_1.getActivationSteepness();
        mutator.mutate(_network_1);
        double biasAfter = _inputNode_1.getBias();
        double activationSteepnessAfter = _inputNode_1.getActivationSteepness();
        double absoluteDifferenceBias = Math.abs(biasBefore - biasAfter);
        double absoluteDifferenceActivationSteepness = Math.abs(activationSteepnessBefore - activationSteepnessAfter);

        assertTrue(absoluteDifferenceBias > 0);
        assertTrue(absoluteDifferenceActivationSteepness > 0);
        assertTrue(absoluteDifferenceBias <= 0.001);
        assertTrue(absoluteDifferenceActivationSteepness <= 0.01);
    }

    @Test
    void forcedNodeParameterMutationRespectsParameterLimits() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                0.5,
                0.5,
                100000,
                100000,
                1000,
                false
        );
        for (int i = 0; i < 1000; i++) {
            mutator.mutate(_network_1);
            double bias = _outputNode_1.getBias();
            double activationSteepness = _outputNode_1.getActivationSteepness();

            assertTrue(bias >=  -1);
            assertTrue(bias <=  2);
            assertTrue(activationSteepness >=  0.3);
            assertTrue(activationSteepness <=  2);
        }
    }

    @Test
    void forcedNodeParameterMutationGeneratesNewRandomParameters() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                0.005,
                0.005,
                1000,
                false
        );
        mutator.mutate(_network_1);

        assertFalse(_inputNode_1.getBias() <= 0.81 && _inputNode_1.getBias() >= 0.79);
        assertFalse(_inputNode_1.getActivationSteepness() <= 1.89 && _inputNode_1.getActivationSteepness() >= 1.87);
    }

    @Test
    void forcedNodeParameterMutationAffectsWeightsAtTheCorrectProportion() {
        NetworkMutator mutator = new NetworkMutator(
                _networkCreator,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                0.688,
                0.7,
                0.2,
                10,
                10,
                1000,
                false
        );
        int amountOfBiasesAffected = 0;
        int amountOfActivationSteepnessesAffected = 0;

        for (int i = 0; i < 1000; i++) {
            _inputNode_1.setBias(-0.3);
            _inputNode_1.setActivationSteepness(0.8);

            mutator.mutate(_network_1);

            if (_inputNode_1.getBias() != -0.3){
                amountOfBiasesAffected++;
            }
            if (_inputNode_1.getActivationSteepness() != 0.8){
                amountOfActivationSteepnessesAffected++;
            }
        }

        // 99,7% confidence interval
        assertEquals(688, amountOfBiasesAffected, 45);
        assertEquals(688, amountOfActivationSteepnessesAffected, 45);
    }

    @Test
    void forcedAddNodeMutationDoesNothingOnConnectionlessNetwork() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        _mutatorForForcedAddNodeMutation.mutate(network);

        List<Node> nodes = network.getNodesSorted();
        List<Connection> connectionsSorted = network.getConnectionsSorted();

        assertEquals(2, nodes.size());
        assertEquals(0, connectionsSorted.size());
    }

    @Test
    void forcedAddNodeMutationResultsInThreeNodes(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes = _network_1.getNodesSorted();

        assertEquals(3, nodes.size());
    }

    @Test
    void forcedAddNodeMutationInsertsNewNodeCorrectly(){
        _mutatorForForcedAddNodeMutation.mutate(_network_1);
        List<Node> nodes = _network_1.getNodesSorted();

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
        List<Node> nodes= _network_1.getNodesSorted();
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
        List<Node> nodes = _network_1.getNodesSorted();
        Node newNode = nodes.get(2);

        assertEquals(1.88, newNode.getActivationSteepness());
        assertEquals(0.8, newNode.getBias());
    }

    @Test
    void fullyConnectedNetworkMakesAddConnectionMutationFallBackToConnectionWeightMutation() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        int inputNodeId = network.getNodesSorted().get(0).getInnovationNumber();
        int outputNodeId = network.getNodesSorted().get(1).getInnovationNumber();

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