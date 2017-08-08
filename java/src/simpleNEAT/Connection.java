package simpleNEAT;

public class Connection {

    private int _innovationNumber;
    private int _nodeOut;
    private int _nodeIn;
    private double _weight;
    private boolean _disabled;

    /**
     * @param nodeOut The innovation number of the node this connections goes out of.
     * @param nodeIn The innovation number of the node this connections goes into.
     * @pre innovationNumber >= 0
     */
    public Connection(int innovationNumber, int nodeOut, int nodeIn, double weight, boolean disabled) {
        assert innovationNumber >= 0;

        _innovationNumber = innovationNumber;
        _nodeOut = nodeOut;
        _nodeIn = nodeIn;
        _weight = weight;
        _disabled = disabled;
    }

    public int getInnovationNumber() {
        return _innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        _innovationNumber = innovationNumber;
    }

    public int getNodeOut() {
        return _nodeOut;
    }

    public void setNodeOut(int nodeOut) {
        _nodeOut = nodeOut;
    }

    public int getNodeIn() {
        return _nodeIn;
    }

    public void setNodeIn(int nodeIn) {
        _nodeIn = nodeIn;
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
