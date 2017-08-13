package simpleNEAT;
import simpleNEAT.Innovation.*;
import simpleNEAT.NeuralNetwork.*;

import java.util.List;
import java.util.Map;

public class NetworkMutator {

    NetworkCreator _networkCreator;

    double _weightMutationProbability;
    double _nodeParameterMutationProbability;
    double _addNodeMutationProbability;
    double _addConnectionMutationProbability;

    int _connectionMutationMaxTries;
    boolean _fallBackToWeightMutationOnConnectionMutationFail;
    double _weightPerturbanceMagnitude;

    int _currentGeneration;
    Map<Innovation, Integer> _innovationNumbers;
    int _latestInnovationNumber;

    /**
     *
     * @param network Must contain at least two nodes and one connection.
     */
    public void maybeMutate(NeuralNetwork network) {
        if (RandomUtil.getRandomBoolean(_weightMutationProbability)) {
            performWeightMutation(network);
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

    private void performWeightMutation(NeuralNetwork network) {
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

            if (network.hasConnectionBetween(nodeOutOfId, nodeIntoId)){
                newConnectionIdentified = true;
            }
        }

        if (newConnectionIdentified){
            addConnection(network, nodeOutOfId, nodeIntoId);
        } else if (_fallBackToWeightMutationOnConnectionMutationFail){
            performWeightMutation(network);
        }
    }

    private void addConnection(NeuralNetwork network, int nodeOutOfId, int nodeIntoId) {
        ConnectionInnovation innovation = new ConnectionInnovation(_currentGeneration, nodeOutOfId, nodeIntoId);
        int innovationNumber = determineInnovationNumber(innovation);
        Connection newConnection = _networkCreator.createConnectionWithRandomWeight(
                innovationNumber, nodeOutOfId, nodeIntoId
        );

        network.addConnection(newConnection);
    }


    private void performAddNodeMutation(NeuralNetwork network) {
        Connection connectionToSplit = RandomUtil.sampleFrom(network.getConnectionsSorted());
        NodeInnovation innovation = new NodeInnovation(_currentGeneration, connectionToSplit.getInnovationNumber());
        int innovationNumber = determineInnovationNumber(innovation);
        Node newNode = _networkCreator.createNodeWithDefaultAttributes(innovationNumber);

        network.addNode(newNode);
    }

    private int determineInnovationNumber(Innovation innovation){
        Integer innovationNumber = _innovationNumbers.get(innovation);

        if (innovationNumber == null){
            _latestInnovationNumber++;
            innovationNumber = _latestInnovationNumber;
            _innovationNumbers.put(innovation, innovationNumber);
        }

        return innovationNumber;
    }
}
