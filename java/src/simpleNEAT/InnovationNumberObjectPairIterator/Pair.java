package simpleNEAT.InnovationNumberObjectPairIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pair<T> {

    private ArrayList<T> _listOfElements;

    /**
     * At most one of {@code object0}, {@code object1} may be null.
     */
    public Pair(T object0, T object1) {
        assert (object0 != null) || (object1 != null) : "At most one of object0, object1 may be null";

        _listOfElements = new ArrayList<>(2);
        _listOfElements.add(object0);
        _listOfElements.add(object1);
    }

    public List<T> toList(){
        return Collections.unmodifiableList(_listOfElements);
    }

    public boolean hasNullElement(){
        return (_listOfElements.get(0) == null) || (_listOfElements.get(1) == null);
    }

    /**
     * Requires that {@code this.hasNullElement()} is true.
     * @return The index of the unique element in the pair that is not null.
     */
    public int indexOfUniqueNonNullElement(){
        assert hasNullElement() : "The pair does not contain a unique non-null element.";
        T object0 = _listOfElements.get(0);

        if (object0 != null){
            return 0;
        }

        return 1;
    }

    public T get(int index){
        return _listOfElements.get(index);
    }
}
