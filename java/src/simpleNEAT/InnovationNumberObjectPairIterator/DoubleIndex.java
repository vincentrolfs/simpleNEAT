package simpleNEAT.InnovationNumberObjectPairIterator;

import java.util.List;

class DoubleIndex {
    private final int[] _indices;

    /**
     * A DoubleIndex is a tuple that contains two indices for two different lists.
     * The two lists will normally be contained in a {@code Pair<List<T>>}.
     */
    DoubleIndex() {
        _indices = new int[] {0, 0};
    }

    /**
     * Requires {@code this.isPartlyInRange(pairOfLists)}
     * @return The pair of elements the given lists yield when accessed at the positions given by this multiindex. If one of the indices
     * in the multiindex is not in the range of the corresponding list, {@code null} is put at that position.
     */
    <T> Pair<T> getValuesOf(Pair<List<T>> pairOfLists) {
        assert isPartlyInRange(pairOfLists) : "Multiindex is not partly in range of the pair of lists.";

        return new Pair<>(
                getSingleValueOf(pairOfLists.get(0), _indices[0]),
                getSingleValueOf(pairOfLists.get(1), _indices[1])
        );
    }

    /**
     * Determines if the double index is at at least one position in the range of the corresponding list
     * in @{code pairOfLists}.
     */
    <T> boolean isPartlyInRange(Pair<List<T>> pairOfLists) {
        return _indices[0] < pairOfLists.get(0).size() || _indices[1] < pairOfLists.get(1).size();
    }

    /**
     * Increase multiindex everywhere by 1.
     */
    void increaseBoth() {
        _indices[0]++;
        _indices[1]++;
    }

    /**
     * Increase double index by 1 at the given position.
     * @param position Must between 0 and 1 inclusive.
     */
    void increaseAt(int position) {
        _indices[position]++;
    }

    private <T> T getSingleValueOf(List<T> list, int index){
        if (index < list.size()){
            return list.get(index);
        } else {
            return null;
        }
    }
}