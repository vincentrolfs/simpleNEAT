package simpleNEAT.Innovation;

public class NodeInnovation extends Innovation {

    private int _connectionSplitId;

    /**
     * @param generationCreated Must benon
     * @param connectionSplitId Must be non-negative.
     */
    public NodeInnovation(int generationCreated, int connectionSplitId) {
        super(generationCreated);
        assert  connectionSplitId >= 0;

        this._connectionSplitId = connectionSplitId;
    }

    public int getConnectionSplitId() {
        return _connectionSplitId;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof NodeInnovation) &&
                ((NodeInnovation) object)._connectionSplitId == _connectionSplitId;
    }

    @Override
    public int hashCode() {
        return _connectionSplitId;
    }
}
