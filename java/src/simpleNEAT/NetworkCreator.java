package simpleNEAT;
import simpleNEAT.Innovation.ConnectionInnovation;
import simpleNEAT.Innovation.Innovation;
import simpleNEAT.Innovation.NodeInnovation;
import simpleNEAT.NeuralNetwork.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NetworkCreator {
    private int _amountInputNodes;
    private int _amountOutputNodes;

    private double _connectionWeightMin;
    private double _connectionWeightMax;

    private double _nodeBiasMin;
    private double _nodeBiasMax;
    private double _defaultNodeBias;

    private double _nodeActivationSteepnessMin;
    private double _nodeActivationSteepnessMax;
    private double _defaultNodeActivationSteepness;

    private int _amountOfGenerationsRememberedForInnovationNumbers;

    int _currentGeneration;
    HashMap<Innovation, Integer> _innovationNumbers;
    int _latestInnovationNumber;

    /**
     * @param amountInputNodes Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     * @param connectionWeightMin Must satisfy {@code connectionWeightMin <= connectionWeightMax}.
     * @param nodeBiasMin Must satisfy {@code nodeBiasMin <= nodeBiasMax}.
     * @param nodeActivationSteepnessMin Must satisfy {@code nodeActivationSteepnessMin <= nodeActivationSteepnessMax}.
     * @param amountOfGenerationsRememberedForInnovationNumbers Must be non-negative.
     */
    public NetworkCreator(int amountInputNodes, int amountOutputNodes, double connectionWeightMin, double connectionWeightMax, double nodeBiasMin, double nodeBiasMax, double defaultNodeBias, double nodeActivationSteepnessMin, double nodeActivationSteepnessMax, double defaultNodeActivationSteepness, int amountOfGenerationsRememberedForInnovationNumbers) {
        assert amountInputNodes >= 1 &&
                amountOutputNodes >= 1 &&
                connectionWeightMin <= connectionWeightMax &&
                nodeBiasMin <= nodeBiasMax &&
                nodeActivationSteepnessMin <= nodeActivationSteepnessMax &&
                amountOfGenerationsRememberedForInnovationNumbers >= 0;

        _amountInputNodes = amountInputNodes;
        _amountOutputNodes = amountOutputNodes;
        _connectionWeightMin = connectionWeightMin;
        _connectionWeightMax = connectionWeightMax;
        _nodeBiasMin = nodeBiasMin;
        _nodeBiasMax = nodeBiasMax;
        _defaultNodeBias = defaultNodeBias;
        _nodeActivationSteepnessMin = nodeActivationSteepnessMin;
        _nodeActivationSteepnessMax = nodeActivationSteepnessMax;
        _defaultNodeActivationSteepness = defaultNodeActivationSteepness;

        _amountOfGenerationsRememberedForInnovationNumbers = amountOfGenerationsRememberedForInnovationNumbers;

        _currentGeneration = 0;
        _innovationNumbers = new HashMap<>();
        _latestInnovationNumber = 1;
    }

    /**
     * Registers the innovation for the NetworkCreator. Created for testing purposes.
     */
    void registerInnovation(Innovation innovation){
        if (!_innovationNumbers.containsKey(innovation)){
            _latestInnovationNumber++;
            _innovationNumbers.put(innovation, _latestInnovationNumber);
        }
    }

    void nextGeneration(){
        _currentGeneration++;

        Set<Map.Entry<Innovation, Integer>> entrySet = _innovationNumbers.entrySet();
        Iterator<Map.Entry<Innovation, Integer>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Innovation someInnovation = iterator.next().getKey();
            int generationsPassed = _currentGeneration - someInnovation.getGenerationCreated();

            if (generationsPassed > _amountOfGenerationsRememberedForInnovationNumbers){
                iterator.remove();
            }
        }
    }

    /**
     * Creates a non-disabled connection with random weight and the correct innovation number.
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId Must be non-negative.
     */
    Connection createConnectionWithRandomWeight(int nodeOutOfId, int nodeIntoId){
        ConnectionInnovation innovation = new ConnectionInnovation(_currentGeneration, nodeOutOfId, nodeIntoId);
        int innovationNumber = determineInnovationNumber(innovation);
        Connection newConnection = new Connection(
                innovationNumber, nodeOutOfId, nodeIntoId, getRandomConnectionWeight(), false
        );

        return newConnection;
    }

    /**
     * Creates a new node with default attributes and the correct innovation number.
     * @param connectionSplitId Must be non-negative.
     */
    Node createNodeWithDefaultAttributes(int connectionSplitId){
        NodeInnovation innovation = new NodeInnovation(_currentGeneration, connectionSplitId);
        int innovationNumber = determineInnovationNumber(innovation);
        Node newNode = new Node(innovationNumber, _defaultNodeActivationSteepness, _defaultNodeBias, false);

        return newNode;
    }

    double getRandomConnectionWeight(){
        return RandomUtil.getRandomDouble(_connectionWeightMin, _connectionWeightMax);
    }

    private int determineInnovationNumber(Innovation innovation){
        registerInnovation(innovation);
        return _innovationNumbers.get(innovation);
    }
}
