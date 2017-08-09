package simpleNEAT.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

class Node {

    private int _innovationNumber;
    private double _activationSteepness;
    private double _bias;
    private boolean _disabled;

    /**
     * @param innovationNumber Must be non-negative.
     */
    Node(int innovationNumber, double activationSteepness, double bias, boolean disabled) {
        assert innovationNumber >= 0;

        _innovationNumber = innovationNumber;
        _activationSteepness = activationSteepness;
        _bias = bias;
        _disabled = disabled;
    }

    int getInnovationNumber() {
        return _innovationNumber;
    }

    double getActivationSteepness() {
        return _activationSteepness;
    }

    void setActivationSteepness(double activationSteepness) {
        _activationSteepness = activationSteepness;
    }

    double getBias() {
        return _bias;
    }

    void setBias(double bias) {
        _bias = bias;
    }

    boolean isDisabled() {
        return _disabled;
    }
}
