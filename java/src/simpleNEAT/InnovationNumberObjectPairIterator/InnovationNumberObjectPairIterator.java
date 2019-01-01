package simpleNEAT.InnovationNumberObjectPairIterator;

import simpleNEAT.NeuralNetwork.InnovationNumberObject;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class InnovationNumberObjectPairIterator<T extends InnovationNumberObject> implements Iterator<Pair<T>> {
    private Pair<List<T>> _elements;
    private DoubleIndex _index;

    public InnovationNumberObjectPairIterator(List<T> elements0, List<T> elements1) {
        _elements = new Pair<>(elements0, elements1);
        _index = new DoubleIndex();
    }

    @Override
    public boolean hasNext() {
        return _index.isPartlyInRange(_elements);
    }

    @Override
    public Pair<T> next() {
        if (!this.hasNext()){
            throw new NoSuchElementException();
        }

        Pair<T> nextPair = _index.getValuesOf(_elements);
        return this.convertNextPairAndAdjustIndex(nextPair);
    }

    private Pair<T> convertNextPairAndAdjustIndex(Pair<T> nextPair){
        if (nextPair.hasNullElement() || nextPair.get(0).getInnovationNumber() == nextPair.get(1).getInnovationNumber()){
            _index.increaseBoth();
            return nextPair;
        } else if (nextPair.get(0).getInnovationNumber() < nextPair.get(1).getInnovationNumber()){
            _index.increaseAt(0);
            return new Pair<>(nextPair.get(0), null);
        } else {
            _index.increaseAt(1);
            return new Pair<>(null, nextPair.get(1));
        }
    }
}
