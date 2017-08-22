package simpleNEAT.Innovation;

public class InputNodeInnovation extends Innovation {

    int _nodeNumber;

    /**
     * @param generationCreated The generation in which this innovation was created. Must be non-negative.
     * @param nodeNumber        The index of the corresponding InputNode in the List of all InputNodes.
     */
    public InputNodeInnovation(int generationCreated, int nodeNumber) {
        super(generationCreated);
        _nodeNumber = nodeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputNodeInnovation that = (InputNodeInnovation) o;

        return _nodeNumber == that._nodeNumber;
    }

    @Override
    public int hashCode() {
        return _nodeNumber;
    }
}
