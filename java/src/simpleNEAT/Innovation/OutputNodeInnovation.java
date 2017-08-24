package simpleNEAT.Innovation;

public class OutputNodeInnovation extends Innovation {

    final int _nodeNumber;

    /**
     * @param generationCreated The generation in which this innovation was created. Must be non-negative.
     * @param nodeNumber        The index of the corresponding OutputNode in the List of all InputNodes.
     */
    public OutputNodeInnovation(int generationCreated, int nodeNumber) {
        super(generationCreated);
        _nodeNumber = nodeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutputNodeInnovation that = (OutputNodeInnovation) o;

        return _nodeNumber == that._nodeNumber;
    }

    @Override
    public int hashCode() {
        return _nodeNumber;
    }
}
