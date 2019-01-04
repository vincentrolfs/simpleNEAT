package simpleNEAT.Innovation;

public class HiddenNodeInnovation extends Innovation {

    private int _connectionSplitId;

    /**
     * @param generationCreated The generation in which this innovation was created. Must be non-negative.
     * @param connectionSplitId Must be non-negative.
     */
    public HiddenNodeInnovation(int generationCreated, int connectionSplitId) {
        super(generationCreated);
        assert connectionSplitId >= 0;

        _connectionSplitId = connectionSplitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenNodeInnovation that = (HiddenNodeInnovation) o;

        return _connectionSplitId == that._connectionSplitId;
    }

    @Override
    public int hashCode() {
        return _connectionSplitId;
    }
}
