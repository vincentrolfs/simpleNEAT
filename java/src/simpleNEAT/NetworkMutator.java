package simpleNEAT;
import simpleNEAT.NeuralNetwork.*;

import java.util.*;

class NetworkMutator {

    private final NetworkCreator _networkCreator;

    private final double _connectionDisabledMutationProbability;
    private final double _connectionWeightMutationProbability;
    private final double _nodeParameterMutationProbability;
    private final double _addNodeMutationProbability;
    private final double _addConnectionMutationProbability;

    private final double _connectionDisabledMutation_connectionAffectedProbability;

    private final double _connectionWeightMutation_connectionAffectedProbability;
    private final double _connectionWeightMutation_newRandomWeightProbability;
    private final double _connectionWeightMutation_maxWeightPerturbanceMagnitude;

    private final double _nodeParameterMutation_nodeAffectedProbability;
    private final double _nodeParameterMutation_newRandomBiasProbability;
    private final double _nodeParameterMutation_newRandomActivationSteepnessProbability;
    private final double _nodeParameterMutation_maxBiasPerturbanceMagnitude;
    private final double _nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude;

    private final int _addConnectionMutation_maxTriesForConnectionSelection;
    private final boolean _addConnectionMutation_fallBackToConnectionWeightMutationOnFail;

    /**
     /**
     * Class for mutating neural networks. Below is a list of possible mutations. All of them have a certain probability
     * of happening, as specifed by the parameters _"mutationName"Probability.
     * Note: A perturbation means adding a small quantity to an existing value, the maximum absolute value of that
     * quantity is here specified by the _mutationName_max*PerturbanceMagnitude parameters.
     *
     * @param networkCreator
     * @param connectionDisabledMutationProbability Must be between 0 and 1 inclusive. Mutation effect: Each connection in the network, if it is affected, has its "disabled" state toggled.
     * @param connectionWeightMutationProbability Must be between 0 and 1 inclusive. Mutation effect: Each connection in the network, should it be affected, has its weight either pertubed or gets a completely new random weight (probabilities of these outcomes are set via the other parameters).
     * @param nodeParameterMutationProbability Must be between 0 and 1 inclusive. Mutation effect: Each node in the network, should it be affected, has its bias either pertubed or gets a completely new random bias, same for the activation steepness.
     * @param addNodeMutationProbability Must be between 0 and 1 inclusive. Mutation effect: Finds a random connection in the network (if one exists), and splits it by effectively putting a node in the middle of the connection. That means that two new connections are created, and the original connection chosen is disabled.
     * @param addConnectionMutationProbability Must be between 0 and 1 inclusive. Mutation effect: Finds two random unconnected nodes and adds a new connection between them with a random weight. It tries {@code _addConnectionMutation_maxTriesForConnectionSelection}-times to find a place for the new connection. If that fails and if {@code addConnectionMutation_fallBackToConnectionWeightMutationOnFail} is true, a connectionweightMutation is performed.
     *
     * @param connectionDisabledMutation_connectionAffectedProbability Must be between 0 and 1 inclusive.
     *
     * @param connectionWeightMutation_connectionAffectedProbability Must be between 0 and 1 inclusive.
     * @param connectionWeightMutation_newRandomWeightProbability Must be between 0 and 1 inclusive.
     * @param connectionWeightMutation_maxWeightPerturbanceMagnitude Must be non-negative.
     *
     * @param nodeParameterMutation_nodeAffectedProbability Must be between 0 and 1 inclusive.
     * @param nodeParameterMutation_newRandomBiasProbability Must be between 0 and 1 inclusive.
     * @param nodeParameterMutation_newRandomActivationSteepnessProbability Must be between 0 and 1 inclusive.
     * @param nodeParameterMutation_maxBiasPerturbanceMagnitude Must be non-negative.
     *
     * @param nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude Must be non-negative.
     * @param addConnectionMutation_maxTriesForConnectionSelection Must be at least 1.
     * @param addConnectionMutation_fallBackToConnectionWeightMutationOnFail
     */
    NetworkMutator(NetworkCreator networkCreator, double connectionDisabledMutationProbability, double connectionWeightMutationProbability, double nodeParameterMutationProbability, double addNodeMutationProbability, double addConnectionMutationProbability, double connectionDisabledMutation_connectionAffectedProbability, double connectionWeightMutation_connectionAffectedProbability, double connectionWeightMutation_newRandomWeightProbability, double connectionWeightMutation_maxWeightPerturbanceMagnitude, double nodeParameterMutation_nodeAffectedProbability, double nodeParameterMutation_newRandomBiasProbability, double nodeParameterMutation_newRandomActivationSteepnessProbability, double nodeParameterMutation_maxBiasPerturbanceMagnitude, double nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude, int addConnectionMutation_maxTriesForConnectionSelection, boolean addConnectionMutation_fallBackToConnectionWeightMutationOnFail) {
        assert 0 <= connectionDisabledMutationProbability && connectionDisabledMutationProbability <= 1 &&
                0 <= connectionWeightMutationProbability && connectionWeightMutationProbability <= 1 &&
                0 <= nodeParameterMutationProbability && nodeParameterMutationProbability <= 1 &&
                0 <= addNodeMutationProbability && addNodeMutationProbability <= 1 &&
                0 <= addConnectionMutationProbability && addConnectionMutationProbability <= 1 &&

                0 <= connectionDisabledMutation_connectionAffectedProbability && connectionDisabledMutation_connectionAffectedProbability <= 1 &&

                0 <= connectionWeightMutation_connectionAffectedProbability && connectionWeightMutation_connectionAffectedProbability <= 1 &&
                0 <= connectionWeightMutation_newRandomWeightProbability && connectionWeightMutation_newRandomWeightProbability <= 1 &&
                0 <= connectionWeightMutation_maxWeightPerturbanceMagnitude &&

                0 <= nodeParameterMutation_nodeAffectedProbability && nodeParameterMutation_nodeAffectedProbability <= 1 &&
                0 <= nodeParameterMutation_newRandomBiasProbability && nodeParameterMutation_newRandomBiasProbability <= 1 &&
                0 <= nodeParameterMutation_newRandomActivationSteepnessProbability && nodeParameterMutation_newRandomActivationSteepnessProbability <= 1 &&
                0 <= nodeParameterMutation_maxBiasPerturbanceMagnitude &&
                0 <= nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude &&

                1 <= addConnectionMutation_maxTriesForConnectionSelection;

        _networkCreator = networkCreator;

        _connectionDisabledMutationProbability = connectionDisabledMutationProbability;
        _connectionWeightMutationProbability = connectionWeightMutationProbability;
        _nodeParameterMutationProbability = nodeParameterMutationProbability;
        _addNodeMutationProbability = addNodeMutationProbability;
        _addConnectionMutationProbability = addConnectionMutationProbability;

        _connectionDisabledMutation_connectionAffectedProbability = connectionDisabledMutation_connectionAffectedProbability;

        _connectionWeightMutation_connectionAffectedProbability = connectionWeightMutation_connectionAffectedProbability;
        _connectionWeightMutation_newRandomWeightProbability = connectionWeightMutation_newRandomWeightProbability;
        _connectionWeightMutation_maxWeightPerturbanceMagnitude = connectionWeightMutation_maxWeightPerturbanceMagnitude;

        _nodeParameterMutation_nodeAffectedProbability = nodeParameterMutation_nodeAffectedProbability;
        _nodeParameterMutation_newRandomBiasProbability = nodeParameterMutation_newRandomBiasProbability;
        _nodeParameterMutation_newRandomActivationSteepnessProbability = nodeParameterMutation_newRandomActivationSteepnessProbability;
        _nodeParameterMutation_maxBiasPerturbanceMagnitude = nodeParameterMutation_maxBiasPerturbanceMagnitude;
        _nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude = nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude;

        _addConnectionMutation_maxTriesForConnectionSelection = addConnectionMutation_maxTriesForConnectionSelection;
        _addConnectionMutation_fallBackToConnectionWeightMutationOnFail = addConnectionMutation_fallBackToConnectionWeightMutationOnFail;
    }

    /**
     * Introduces mutation into network by random chance. It is possible that no mutation occurs.
     * @param network Must contain at least one input and one output node.
     */
    void mutate(NeuralNetwork network) {
        if (RandomUtil.getRandomBoolean(_connectionDisabledMutationProbability)) {
            performConnectionDisabledMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_addNodeMutationProbability)) {
            performAddNodeMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_addConnectionMutationProbability)) {
            performAddConnectionMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_connectionWeightMutationProbability)) {
            performConnectionWeightMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_nodeParameterMutationProbability)) {
            performNodeParameterMutation(network);
        }
    }

    private void performConnectionDisabledMutation(NeuralNetwork network){
        List<Connection> allConnections = network.getConnectionsSorted();

        for (Connection someConnection : allConnections){
            if (RandomUtil.getRandomBoolean(_connectionDisabledMutation_connectionAffectedProbability)) {
                boolean connectionDisabled = someConnection.isDisabled();
                someConnection.setDisabled(!connectionDisabled);
            }
        }
    }

    private void performConnectionWeightMutation(NeuralNetwork network) {
        List<Connection> allConnections = network.getConnectionsSorted();

        for (Connection someConnection : allConnections){
            if (RandomUtil.getRandomBoolean(_connectionWeightMutation_connectionAffectedProbability)) {
                performConnectionWeightMutation_onSpecificConnection(someConnection);
            }
        }
    }

    private void performConnectionWeightMutation_onSpecificConnection(Connection connection){
        if (RandomUtil.getRandomBoolean(_connectionWeightMutation_newRandomWeightProbability)){
            connection.setWeight(_networkCreator.getRandomConnectionWeight());
        } else {
            performConnectionWeightPerturbance(connection);
        }
    }

    private void performNodeParameterMutation(NeuralNetwork network) {
        List<Node> allNodes = network.getNodes();

        for (Node someNode : allNodes){
            if (RandomUtil.getRandomBoolean(_nodeParameterMutation_nodeAffectedProbability)) {
                performNodeParameterMutation_onSpecificNode(someNode);
            }
        }
    }

    private void performNodeParameterMutation_onSpecificNode(Node node) {
        if (RandomUtil.getRandomBoolean(_nodeParameterMutation_newRandomBiasProbability)){
            node.setBias(_networkCreator.getRandomNodeBias());
        } else {
            performNodeBiasPerturbance(node);
        }

        if (RandomUtil.getRandomBoolean(_nodeParameterMutation_newRandomActivationSteepnessProbability)){
            node.setActivationSteepness(_networkCreator.getRandomNodeActivationSteepness());
        } else {
            performNodeActivationSteepnessPerturbance(node);
        }
    }

    private void performAddNodeMutation(NeuralNetwork network) {
        List<Connection> allConnections = network.getConnectionsSorted();

        if (allConnections.size() == 0){
            return;
        }

        Connection connectionToSplit = RandomUtil.sampleFrom(allConnections);
        int connectionToSplitId = connectionToSplit.getInnovationNumber();

        Node newNode = _networkCreator.createNodeWithDefaultAttributes(connectionToSplitId);
        int newNodeId = newNode.getInnovationNumber();

        Connection newConnectionIn = _networkCreator.createConnectionWithDefaultWeight(
                connectionToSplit.getNodeOutOfId(),
                newNodeId
        );
        Connection newConnectionOut = _networkCreator.createConnectionWithGivenWeight(
                newNodeId,
                connectionToSplit.getNodeIntoId(),
                connectionToSplit.getWeight()
        );

        connectionToSplit.setDisabled(true);
        network.addNode(newNode);
        network.addConnection(newConnectionIn);
        network.addConnection(newConnectionOut);
    }

    private void performAddConnectionMutation(NeuralNetwork network) {
        List<Node> nodes = network.getNodes();
        int nodeOutOfId = -1;
        int nodeIntoId = -1;
        boolean newConnectionIdentified = false;

        for (int i = 1; !newConnectionIdentified && i <= _addConnectionMutation_maxTriesForConnectionSelection; i++){
            nodeOutOfId = RandomUtil.sampleFrom(nodes).getInnovationNumber();
            nodeIntoId = RandomUtil.sampleFrom(nodes).getInnovationNumber();

            if (!network.hasConnectionBetween(nodeOutOfId, nodeIntoId)){
                newConnectionIdentified = true;
            }
        }

        if (newConnectionIdentified){
            addConnection(network, nodeOutOfId, nodeIntoId);
        } else if (_addConnectionMutation_fallBackToConnectionWeightMutationOnFail){
            performConnectionWeightMutation(network);
        }
    }

    private void addConnection(NeuralNetwork network, int nodeOutOfId, int nodeIntoId) {
        Connection newConnection = _networkCreator.createConnectionWithRandomWeight(nodeOutOfId, nodeIntoId);
        network.addConnection(newConnection);
    }

    private void performConnectionWeightPerturbance(Connection connection){
        double oldWeight = connection.getWeight();
        double perturbedWeight = perturbValue(oldWeight, _connectionWeightMutation_maxWeightPerturbanceMagnitude);
        double newWeight = _networkCreator.capConnectionWeight(perturbedWeight);

        connection.setWeight(newWeight);
    }

    private void performNodeBiasPerturbance(Node node){
        double oldBias = node.getBias();
        double perturbedBias = perturbValue(oldBias, _nodeParameterMutation_maxBiasPerturbanceMagnitude);
        double newBias = _networkCreator.capNodeBias(perturbedBias);

        node.setBias(newBias);
    }

    private void performNodeActivationSteepnessPerturbance(Node node){
        double oldActivationSteepness = node.getActivationSteepness();
        double perturbedActivationSteepness = perturbValue(
                oldActivationSteepness,
                _nodeParameterMutation_maxActivationSteepnessPerturbanceMagnitude
        );
        double newActivationSteepness = _networkCreator.capNodeActivationSteepness(perturbedActivationSteepness);

        node.setActivationSteepness(newActivationSteepness);
    }

    private double perturbValue(double value, double maxPerturbanceMagnitude){
        double perturbance = RandomUtil.getRandomDouble(-1, 1) * maxPerturbanceMagnitude;

        return value + perturbance;
    }
}
