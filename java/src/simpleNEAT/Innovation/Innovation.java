package simpleNEAT.Innovation;

public abstract class Innovation {

    int _generationCreated;

    /**
     * @param generationCreated The generation in which this innovation was created. Must be non-negative.
     */
    Innovation(int generationCreated) {
        assert generationCreated >= 0;

        _generationCreated = generationCreated;
    }

    public int getGenerationCreated() {
        return _generationCreated;
    }

    public abstract boolean equals(Object object);
    public abstract int hashCode();

}
