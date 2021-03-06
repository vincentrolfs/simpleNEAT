package simpleNEAT;

import simpleNEAT.Innovation.*;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.*;

public class NetworkCreator {

    private int _amountInputNodes;
    private int _amountOutputNodes;

    private double _connectionWeightMin;
    private double _connectionWeightMax;
    private double _defaultConnectionWeight;

    private double _nodeBiasMin;
    private double _nodeBiasMax;
    private double _defaultNodeBias;

    private double _nodeActivationSteepnessMin;
    private double _nodeActivationSteepnessMax;
    private double _defaultNodeActivationSteepness;

    private int _amountOfGenerationsRememberedForInnovationNumbers;

    private int _currentGeneration;
    private HashMap<Innovation, Integer> _innovationNumbers;
    private int _latestInnovationNumber;

    /**
     * Creates new NetworkCreator. {@code defaultNodeBias} and
     * {@code _defaultNodeActivationSteepness} are recommended to be chosen so that they act as the identity on their input.
     * @param amountInputNodes                                  Must be at least 1.
     * @param amountOutputNodes                                 Must be at least 1.
     * @param connectionWeightMin                               Must satisfy {@code connectionWeightMin <= connectionWeightMax}.
     * @param defaultConnectionWeight                           Is used when a new connection is made during a node
     *                                                          innovation is recommended to be close to 1.
     * @param nodeBiasMin                                       Must satisfy {@code nodeBiasMin <= nodeBiasMax}.
     * @param nodeActivationSteepnessMin                        Must satisfy {@code nodeActivationSteepnessMin <= nodeActivationSteepnessMax}.
     * @param amountOfGenerationsRememberedForInnovationNumbers Must be non-negative.
     */
    public NetworkCreator(int amountInputNodes, int amountOutputNodes, double connectionWeightMin, double connectionWeightMax, double defaultConnectionWeight, double nodeBiasMin, double nodeBiasMax, double defaultNodeBias, double nodeActivationSteepnessMin, double nodeActivationSteepnessMax, double defaultNodeActivationSteepness, int amountOfGenerationsRememberedForInnovationNumbers) {
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
        _defaultConnectionWeight = defaultConnectionWeight;

        _nodeBiasMin = nodeBiasMin;
        _nodeBiasMax = nodeBiasMax;
        _defaultNodeBias = defaultNodeBias;

        _nodeActivationSteepnessMin = nodeActivationSteepnessMin;
        _nodeActivationSteepnessMax = nodeActivationSteepnessMax;
        _defaultNodeActivationSteepness = defaultNodeActivationSteepness;

        _amountOfGenerationsRememberedForInnovationNumbers = amountOfGenerationsRememberedForInnovationNumbers;

        _currentGeneration = 0;
        _innovationNumbers = new HashMap<>();
        _latestInnovationNumber = 0;
    }

    void nextGeneration() {
        _currentGeneration++;

        Set<Map.Entry<Innovation, Integer>> entrySet = _innovationNumbers.entrySet();
        Iterator<Map.Entry<Innovation, Integer>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Innovation someInnovation = iterator.next().getKey();
            int generationsPassed = _currentGeneration - someInnovation.getGenerationCreated();

            if (generationsPassed > _amountOfGenerationsRememberedForInnovationNumbers) {
                iterator.remove();
            }
        }
    }

    /**
     * Signifies whether the given connection weight is in the allowed range.
     */
    boolean isInConnectionWeightRange(double connectionWeight) {
        return _connectionWeightMin <= connectionWeight && connectionWeight <= _connectionWeightMax;
    }

    /**
     * Caps connectionWeight so that it is between connectionWeightMin and connectionWeightMax
     */
    double capConnectionWeight(double connectionWeight) {
        return capDouble(connectionWeight, _connectionWeightMin, _connectionWeightMax);
    }

    /**
     * Caps nodeBias so that it is between nodeBiasMin and nodeBiasMax
     */
    double capNodeBias(double nodeBias) {
        return capDouble(nodeBias, _nodeBiasMin, _nodeBiasMax);
    }

    /**
     * Caps capNodeActivationSteepness so that it is between nodeActivationSteepnessMin and nodeActivationSteepnessMax
     */
    double capNodeActivationSteepness(double nodeActivationSteepness) {
        return capDouble(nodeActivationSteepness, _nodeActivationSteepnessMin, _nodeActivationSteepnessMax);
    }

    /**
     * Creates a non-disabled connection with the given weight and the correct innovation number.
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId  Must be non-negative.
     * @param weight      Must satisfy isInConnectionWeightRange(weight)
     */
    Connection createConnectionWithGivenWeight(int nodeOutOfId, int nodeIntoId, double weight) {
        assert nodeOutOfId >= 0 && nodeIntoId >= 0 && isInConnectionWeightRange(weight);

        ConnectionInnovation innovation = new ConnectionInnovation(_currentGeneration, nodeOutOfId, nodeIntoId);
        int innovationNumber = determineInnovationNumber(innovation);
        Connection newConnection = new Connection(
                innovationNumber, nodeOutOfId, nodeIntoId, weight, false
        );

        return newConnection;
    }

    /**
     * Creates a non-disabled connection with random weight and the correct innovation number.
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId  Must be non-negative.
     */
    Connection createConnectionWithRandomWeight(int nodeOutOfId, int nodeIntoId) {
        return createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, getRandomConnectionWeight());
    }

    /**
     * Creates a non-disabled connection with default weight and the correct innovation number.
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId  Must be non-negative.
     */
    public Connection createConnectionWithDefaultWeight(int nodeOutOfId, int nodeIntoId) {
        return createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, _defaultConnectionWeight);
    }

    /**
     * Creates a new node with default attributes and the correct innovation number.
     * @param connectionSplitId The innovation number of the connection that this new node splits. Must be non-negative.
     */
    Node createNodeWithDefaultAttributes(int connectionSplitId) {
        HiddenNodeInnovation innovation = new HiddenNodeInnovation(_currentGeneration, connectionSplitId);
        return createNodeWithDefaultAttributesFromInnovation(innovation);
    }

    /**
     * Creates a new network consisting only of input and output nodes with default attributes, and no connections.
     */
    public NeuralNetwork createMinimalNeuralNetwork() {
        LinkedList<Node> nodes = createMinimalNodeList();
        LinkedList<Connection> connections = new LinkedList<>();

        return new NeuralNetwork(nodes, connections, _amountInputNodes, _amountOutputNodes);
    }

    double getRandomConnectionWeight() {
        return RandomUtil.getRandomDouble(_connectionWeightMin, _connectionWeightMax);
    }

    double getRandomNodeBias() {
        return RandomUtil.getRandomDouble(_nodeBiasMin, _nodeBiasMax);
    }

    double getRandomNodeActivationSteepness() {
        return RandomUtil.getRandomDouble(_nodeActivationSteepnessMin, _nodeActivationSteepnessMax);
    }

    private int determineInnovationNumber(Innovation innovation) {
        Integer innovationNumber = _innovationNumbers.get(innovation);

        if (innovationNumber == null) {
            _latestInnovationNumber++;
            innovationNumber = _latestInnovationNumber;
            _innovationNumbers.put(innovation, innovationNumber);
        }

        return innovationNumber;
    }

    private LinkedList<Node> createMinimalNodeList(){
        LinkedList<Node> nodes = createInputNodes();
        LinkedList<Node> outputNodes = createOutputNodes();

        nodes.addAll(outputNodes);

        return nodes;
    }

    private LinkedList<Node> createInputNodes() {
        LinkedList<Node> inputNodes = new LinkedList<>();

        for (int i = 0; i < _amountInputNodes; i++) {
            InputNodeInnovation innovation = new InputNodeInnovation(_currentGeneration, i);
            Node oneInputNode = createNodeWithDefaultAttributesFromInnovation(innovation);

            inputNodes.add(oneInputNode);
        }

        return inputNodes;
    }

    private LinkedList<Node> createOutputNodes() {
        LinkedList<Node> outputNodes = new LinkedList<>();

        for (int i = 0; i < _amountOutputNodes; i++) {
            OutputNodeInnovation innovation = new OutputNodeInnovation(_currentGeneration, i);
            Node oneInputNode = createNodeWithDefaultAttributesFromInnovation(innovation);

            outputNodes.add(oneInputNode);
        }

        return outputNodes;
    }

    private Node createNodeWithDefaultAttributesFromInnovation(Innovation innovation) {
        int innovationNumber = determineInnovationNumber(innovation);
        Node newNode = new Node(innovationNumber, _defaultNodeActivationSteepness, _defaultNodeBias);

        return newNode;
    }

    private double capDouble(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
}
