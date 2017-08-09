package simpleNEAT;
import simpleNEAT.NeuralNetwork.*;

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

    /**
     * @param amountInputNodes Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     * @param connectionWeightMin Must satisfy {@code connectionWeightMin <= connectionWeightMax}.
     * @param nodeBiasMin Must satisfy {@code nodeBiasMin <= nodeBiasMax}.
     * @param nodeActivationSteepnessMin Must satisfy {@code nodeActivationSteepnessMin <= nodeActivationSteepnessMax}.
     */
    public NetworkCreator(int amountInputNodes, int amountOutputNodes, double connectionWeightMin, double connectionWeightMax, double nodeBiasMin, double nodeBiasMax, double defaultNodeBias, double nodeActivationSteepnessMin, double nodeActivationSteepnessMax, double defaultNodeActivationSteepness) {
        assert amountInputNodes >= 1 && amountOutputNodes >= 1 && connectionWeightMin <= connectionWeightMax &&
                nodeBiasMin <= nodeBiasMax && nodeActivationSteepnessMin <= nodeActivationSteepnessMax;

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
    }

    Node createNodeWithDefaultAttributes(int innovationNumber){
        return new Node(innovationNumber, _defaultNodeActivationSteepness, _defaultNodeBias, false);
    }

    Connection createConnectionWithRandomWeight(int innovationNumber, int nodeOutOf, int nodeInto){
        return new Connection(innovationNumber, nodeOutOf, nodeInto, getRandomWeight(), false);
    }

    double getRandomWeight(){
        return getRandomDouble(_connectionWeightMin, _connectionWeightMax);
    }

    private double getRandomDouble(double min, double max){
        return RandomUtil.generator.nextDouble() * (max - min) + min;
    }
}
