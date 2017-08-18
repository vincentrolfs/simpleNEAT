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

    public int getInnovationNumber() {
        return _innovationNumber;
    }

    /**
     * @return The innovation number of the node this connections goes out of.
     */
    public int getNodeOutOfId() {
        return _nodeOutOf;
    }

    /**
     * @return The innovation number of the node this connections goes into.
     */
    public int getNodeIntoId() {
        return _nodeInto;
    }

    public double getWeight() {
        return _weight;
    }

    public void setWeight(double weight) {
        _weight = weight;
    }

    public boolean isDisabled() {
        return _disabled;
    }

    public void setDisabled(boolean disabled) {
        _disabled = disabled;
    }
}
