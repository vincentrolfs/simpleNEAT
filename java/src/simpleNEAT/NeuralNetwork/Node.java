package simpleNEAT.NeuralNetwork;

public class Node implements InnovationNumberObject {

    private int _innovationNumber;
    private double _activationSteepness;
    private double _bias;

    /**
     * @param innovationNumber Must be non-negative.
     */
    public Node(int innovationNumber, double activationSteepness, double bias) {
        assert innovationNumber >= 0;

        _innovationNumber = innovationNumber;
        _activationSteepness = activationSteepness;
        _bias = bias;
    }

    public Node(Node node) {
        _innovationNumber = node._innovationNumber;
        _activationSteepness = node._activationSteepness;
        _bias = node._bias;
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
}
