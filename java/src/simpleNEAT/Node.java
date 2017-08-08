package simpleNEAT;

import java.util.HashSet;
import java.util.Set;

public class Node {

    private int _innovationNumber;
    private double _activationSteepness;
    private double _bias;
    private boolean _disabled;
    private Set<Node> _connectedInto;

    /**
     * @pre innovationNumber >= 0
     */
    public Node(int innovationNumber, double activationSteepness, double bias, boolean disabled) {
        assert innovationNumber >= 0;

        _innovationNumber = innovationNumber;
        _activationSteepness = activationSteepness;
        _bias = bias;
        _disabled = disabled;
        _connectedInto = new HashSet<>();
    }

    public int getInnovationNumber() {
        return _innovationNumber;
    }

    public double getActivationSteepness() {
        return _activationSteepness;
    }

    public void setActivationSteepness(double activationSteepness) {
        _activationSteepness = activationSteepness;
    }

    public double getBias() {
        return _bias;
    }

    public void setBias(double bias) {
        _bias = bias;
    }

    public boolean isDisabled() {
        return _disabled;
    }

    public void setDisabled(boolean disabled) {
        _disabled = disabled;
    }

    public void markConnectedInto(Node node) {
        _connectedInto.add(node);
    }

    public boolean isConnectedInto(Node node) {
        return _connectedInto.contains(node);
    }
}
