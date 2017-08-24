package simpleNEAT;

import java.util.ArrayList;
import java.util.List;

class MultiIndex {
    private final int _size;
    private final int[] _indices;

    MultiIndex(int size) {
        _size = size;
        _indices = initializeIndices();
    }

    private int[] initializeIndices() {
        int[] indices = new int[_size];

        for (int i = 0; i < _size; i++) {
            indices[i] = 0;
        }

        return indices;
    }

    int getSize() {
        return _size;
    }

    int getOneIndex(int position){
        return _indices[position];
    }

    /**
     * @param list Must be th size of getSize()
     * @return A list of the given lists evaluated at the position given by this multiindex.
     */
    <T> List<T> getValuesOf(List<List<T>> list) {
        assert list.size() == _size;

        List<T> contents = new ArrayList<T>();

        for (int i = 0; i < _size; i++) {
            int j = _indices[i];
            contents.add(list.get(i).get(j));
        }

        return contents;
    }

    /**
     * Determines if the multiindex is smaller than the given list at every position.
     * @param list Must be the size of getSize();
     */
    boolean isSmallerEverywhere(List<Integer> list) {
        assert list.size() == _size;

        for (int i = 0; i < _size; i++) {
            if (_indices[i] >= list.get(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Increase multiindex everywhere by 1.
     */
    void increaseAll() {
        for (int i = 0; i < _size; i++) {
            increaseAt(i);
        }
    }

    /**
     * Increase multiindex by 1 at the given position.
     * @param position Must between 0 (inclusive) and getSize (exclusive).
     */
    void increaseAt(int position) {
        _indices[position]++;
    }
}
