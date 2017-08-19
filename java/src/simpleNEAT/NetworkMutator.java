package simpleNEAT;
import simpleNEAT.NeuralNetwork.*;

import java.util.*;

public class NetworkMutator {

    private NetworkCreator _networkCreator;

    private double _connectionParameterMutationProbability;
    private double _nodeParameterMutationProbability;
    private double _addNodeMutationProbability;
    private double _addConnectionMutationProbability;

    private int _connectionMutationMaxTries;
    private boolean _fallBackToWeightMutationOnConnectionMutationFail;

    private double _connectionWeightPerturbanceMagnitude;
    private double _nodeBiasPerturbanceMagnitude;
    private double _nodeActivationSteepnessPerturbanceMagnitude;

    public NetworkMutator(NetworkCreator networkCreator, double connectionParameterMutationProbability, double nodeParameterMutationProbability, double addNodeMutationProbability, double addConnectionMutationProbability, int connectionMutationMaxTries, boolean fallBackToWeightMutationOnConnectionMutationFail, double connectionWeightPerturbanceMagnitude, double nodeBiasPerturbanceMagnitude, double nodeActivationSteepnessPerturbanceMagnitude) {
        _networkCreator = networkCreator;

        _connectionParameterMutationProbability = connectionParameterMutationProbability;
        _nodeParameterMutationProbability = nodeParameterMutationProbability;
        _addNodeMutationProbability = addNodeMutationProbability;
        _addConnectionMutationProbability = addConnectionMutationProbability;

        _connectionMutationMaxTries = connectionMutationMaxTries;
        _fallBackToWeightMutationOnConnectionMutationFail = fallBackToWeightMutationOnConnectionMutationFail;

        _connectionWeightPerturbanceMagnitude = connectionWeightPerturbanceMagnitude;
        _nodeBiasPerturbanceMagnitude = nodeBiasPerturbanceMagnitude;
        _nodeActivationSteepnessPerturbanceMagnitude = nodeActivationSteepnessPerturbanceMagnitude;
    }

    /**
     * Introduces mutation into network by random chance. It is possible that no mutation occurs.
     * @param network Must contain at least one input and one output node.
     */
    void mutate(NeuralNetwork network) {
        if (RandomUtil.getRandomBoolean(_connectionParameterMutationProbability)) {
            performConnectionParameterMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_nodeParameterMutationProbability)) {
            performNodeParameterMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_addNodeMutationProbability)) {
            performAddNodeMutation(network);
        }
        if (RandomUtil.getRandomBoolean(_addConnectionMutationProbability)) {
            performAddConnectionMutation(network);
        }
    }

    private void performConnectionParameterMutation(NeuralNetwork network) {
        List<Connection> allConnections = network.getConnectionsSorted();

    }

    private void performNodeParameterMutation(NeuralNetwork network) {
    }

    private void performAddConnectionMutation(NeuralNetwork network) {
        List<Node> nodes = network.getNodes();
        int nodeOutOfId = -1;
        int nodeIntoId = -1;
        boolean newConnectionIdentified = false;

        for (int i = 1; !newConnectionIdentified && i <= _connectionMutationMaxTries; i++){
            nodeOutOfId = RandomUtil.sampleFrom(nodes).getInnovationNumber();
            nodeIntoId = RandomUtil.sampleFrom(nodes).getInnovationNumber();

            if (!network.hasConnectionBetween(nodeOutOfId, nodeIntoId)){
                newConnectionIdentified = true;
            }
        }

        if (newConnectionIdentified){
            addConnection(network, nodeOutOfId, nodeIntoId);
        } else if (_fallBackToWeightMutationOnConnectionMutationFail){
            performConnectionParameterMutation(network);
        }
    }

    private void addConnection(NeuralNetwork network, int nodeOutOfId, int nodeIntoId) {
        Connection newConnection = _networkCreator.createConnectionWithRandomWeight(nodeOutOfId, nodeIntoId);
        network.addConnection(newConnection);
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
}
