package simpleNEAT.NeuralNetwork;

public class Connection {

    private int _innovationNumber;
    private int _nodeOutOf;
    private int _nodeInto;
    private double _weight;
    private boolean _disabled;

    /**
     * @param nodeOutOf The innovation number of the node this connections goes out of. Must be non-negative.
     * @param nodeInto The innovation number of the node this connections goes into. Must be non-negative.
     */
    public Connection(int innovationNumber, int nodeOutOf, int nodeInto, double weight, boolean disabled) {
        assert innovationNumber >= 0;

        _innovationNumber = innovationNumber;
        _nodeOutOf = nodeOutOf;
        _nodeInto = nodeInto;
        _weight = weight;
        _disabled = disabled;
    }

    int getInnovationNumber() {
        return _innovationNumber;
    }

    /**
     * @return The innovation number of the node this connections goes out of.
     */
    int getNodeOutOfId() {
        return _nodeOutOf;
    }

    /**
     * @return The innovation number of the node this connections goes into.
     */
    int getNodeIntoId() {
        return _nodeInto;
    }

    double getWeight() {
        return _weight;
    }

    void setWeight(double weight) {
        _weight = weight;
    }

    boolean isDisabled() {
        return _disabled;
    }
}
