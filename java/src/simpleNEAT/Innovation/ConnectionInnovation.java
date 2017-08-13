package simpleNEAT.Innovation;

public class ConnectionInnovation {
    private int _nodeOutOfId;
    private int _nodeIntoId;

    /**
     * @param nodeOutOfId Must be non-negative.
     * @param nodeIntoId Must be non-negative.
     */
    public ConnectionInnovation(int nodeOutOfId, int nodeIntoId) {
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
