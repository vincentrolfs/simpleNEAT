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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionInnovation that = (ConnectionInnovation) o;

        if (_nodeOutOfId != that._nodeOutOfId) return false;
        return _nodeIntoId == that._nodeIntoId;
    }

    @Override
    public int hashCode() {
        int result = _nodeOutOfId;
        result = 31 * result + _nodeIntoId;
        return result;
    }
}
