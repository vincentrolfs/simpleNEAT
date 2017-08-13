package simpleNEAT.Innovation;

public class ConnectionInnovation extends Innovation{
    private int _nodeOutOfId;
    private int _nodeIntoId;

    /**
     * @param generationCreated The generation in which this innovation was created. Must be non-negative.
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId Must be non-negative.
     */
    public ConnectionInnovation(int generationCreated, int nodeOutOfId, int nodeIntoId) {
        super(generationCreated);
        assert nodeOutOfId >= 0 && nodeIntoId >= 0;

        _nodeOutOfId = nodeOutOfId;
        _nodeIntoId = nodeIntoId;
    }

    public int getNodeOutOfId() {
        return _nodeOutOfId;
    }

    public int getNodeIntoId() {
        return _nodeIntoId;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof ConnectionInnovation) &&
                ((ConnectionInnovation) object)._nodeOutOfId == _nodeOutOfId &&
                ((ConnectionInnovation) object)._nodeIntoId == _nodeIntoId;
    }

    @Override
    public int hashCode() {
        return 31 * _nodeOutOfId + _nodeIntoId;
    }
}
