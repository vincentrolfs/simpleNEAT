package simpleNEAT;

import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.InnovationNumberObject;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.*;

public class NetworkMater {

    private final double _inclusionOfConnectionsFromLessFitParentProbability;

    private class OffspringBuilder {
        LinkedList<Node> _offspringNodes;
        LinkedList<Connection> _offspringConnections;

        OffspringBuilder() {
            _offspringNodes = new LinkedList<>();
            _offspringConnections = new LinkedList<>();
        }

        NeuralNetwork build(int amountInputNodes, int amountOutputNodes){
            return new NeuralNetwork(
                    _offspringNodes,
                    _offspringConnections,
                    amountInputNodes,
                    amountOutputNodes
            );
        }

        /**
         * @param <T> Must be either Node or Connection.
         */
        <T> void addStructure(T structure){
            if (structure instanceof Node){
                addNode(structure);
            } else if (structure instanceof Connection){
                addConnection(structure);
            } else {
                throw new AssertionError("Cannot add structure that is neither Node nor Connection.");
            }
        }

        private <T> void addNode(T structure) {
            Node original = (Node) structure;
            Node clone = new Node(original);
            _offspringNodes.add(clone);
        }

        private <T> void addConnection(T structure) {
            Connection original = (Connection) structure;
            Connection clone = new Connection(original);
            _offspringConnections.add(clone);
        }
    }

    private class DoubleIndex {
        private final int[] _indices;

        /**
         * A DoubleIndex is a tuple that contains two indices for two different lists.
         * The two lists will normally contaned in another list of length two.
         */
        DoubleIndex() {
            _indices = new int[] {0, 0};
        }

        /**
         * @param lists Must be of size 2
         * @return A list of the given lists evaluated at the position given by this multiindex. If one of the indices
         * in the multiindex is not in the range of the corresponding list, {@code null} is put at that position.
         */
        <T> List<T> tryGetValuesOf(List<List<T>> lists) {
            assert lists.size() == 2;

            List<T> values = new ArrayList<T>();

            values.add(tryGetSingleValue(lists.get(0), _indices[0]));
            values.add(tryGetSingleValue(lists.get(1), _indices[1]));

            return values;
        }

        /**
         * Determines if the double index is at at least one position in the range of the corresponding list
         * in @{code lists}.
         * @param lists Must be of size 2.
         */
        <T> boolean isPartlyInRange(List<List<T>> lists) {
            assert lists.size() == 2;

            return _indices[0] < lists.get(0).size() || _indices[1] < lists.get(1).size();
        }

        /**
         * Increase multiindex everywhere by 1.
         */
        void increaseAll() {
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

        private <T> T tryGetSingleValue(List<T> list, int index){
            if (index < list.size()){
                return list.get(index);
            } else {
                return null;
            }
        }
    }

    /**
     * @param inclusionOfConnectionsFromLessFitParentProbability Must be between 0 and 1 inclusive.
     */
    public NetworkMater(double inclusionOfConnectionsFromLessFitParentProbability) {
        assert  inclusionOfConnectionsFromLessFitParentProbability >= 0 &&
                inclusionOfConnectionsFromLessFitParentProbability <= 1;

        this._inclusionOfConnectionsFromLessFitParentProbability = inclusionOfConnectionsFromLessFitParentProbability;
    }

    NeuralNetwork createOffspring(NeuralNetwork parent0, NeuralNetwork parent1) {
        assert parent0.getAmountInputNodes() == parent1.getAmountInputNodes() &&
                parent0.getAmountOutputNodes() == parent1.getAmountOutputNodes();

        int positionOfFitterParent = determinePositionOfFitterParent(parent0, parent1);
        boolean includeStructuresFromLessFitParent = decideInclusionOfConnectionsFromLessFitParent();
        OffspringBuilder offspringBuilder = new OffspringBuilder();

        mateStructures(
                parent0.getNodesSorted(),
                parent1.getNodesSorted(),
                offspringBuilder,
                positionOfFitterParent,
                includeStructuresFromLessFitParent
        );

        mateStructures(
                parent0.getConnectionsSorted(),
                parent1.getConnectionsSorted(),
                offspringBuilder,
                positionOfFitterParent,
                includeStructuresFromLessFitParent
        );

        return offspringBuilder.build(parent0.getAmountInputNodes(), parent0.getAmountOutputNodes());
    }

    /**
     * Returns 0 if the first parent is fitter, 1 if the second parent is fitter, and a random value of either 0 or 1 if
     * they have the same fitness.
     */
    private int determinePositionOfFitterParent(NeuralNetwork parent0, NeuralNetwork parent1) {
        double fitness0 = parent0.getFitness();
        double fitness1 = parent1.getFitness();

        if (fitness0 > fitness1) {
            return 0;
        } else if (fitness0 < fitness1) {
            return 1;
        } else {
            return RandomUtil.generator.nextInt(2);
        }
    }

    private boolean decideInclusionOfConnectionsFromLessFitParent() {
        return RandomUtil.getRandomBoolean(_inclusionOfConnectionsFromLessFitParentProbability);
    }

    private <T extends InnovationNumberObject> void mateStructures(List<T> structures0, List<T> structures1,
                                                                   OffspringBuilder offspringBuilder,
                                                                   int positionOfFitterParent,
                                                                   boolean includeStructuresFromLessFitParent) {
        DoubleIndex doubleIndex = new DoubleIndex();
        List<List<T>> structures = combineIntoList(structures0, structures1);

        while (doubleIndex.isPartlyInRange(structures)) {
            List<T> currentStructures = doubleIndex.tryGetValuesOf(structures);
            List<Integer> innovationNumbers = tryGetInnovationNumbers(currentStructures);

            if (null != innovationNumbers.get(0) && innovationNumbers.get(0).equals(innovationNumbers.get(1))) {
                handleMatchingStructure(offspringBuilder, currentStructures, doubleIndex);
            } else {
                handleNonMatchingStructure(offspringBuilder, currentStructures, doubleIndex,
                        innovationNumbers, positionOfFitterParent, includeStructuresFromLessFitParent);
            }
        }
    }

    private <T> List<T> combineIntoList(T element0, T element1) {
        List<T> list = new ArrayList<>();
        list.add(element0);
        list.add(element1);

        return list;
    }

    /**
     * Returns a list of the innovation numbers of the given objects. If one of the objects is null, the returned
     * list will be null at that position.
     */
    private <T extends InnovationNumberObject> List<Integer> tryGetInnovationNumbers(List<T> structures) {
        List<Integer> innovationNumbers = new ArrayList<>();
        Integer innovationNumber0 = structures.get(0) == null? null : structures.get(0).getInnovationNumber();
        Integer innovationNumber1 = structures.get(1) == null? null : structures.get(1).getInnovationNumber();

        innovationNumbers.add(innovationNumber0);
        innovationNumbers.add(innovationNumber1);

        return innovationNumbers;
    }

    private <T> void handleMatchingStructure(OffspringBuilder offspringBuilder,
                                          List<T> currentStructures, DoubleIndex doubleIndex) {
        T structure = RandomUtil.sampleFrom(currentStructures);
        offspringBuilder.addStructure(structure);
        doubleIndex.increaseAll();
    }

    private <T> void handleNonMatchingStructure(OffspringBuilder offspringBuilder,
                                             List<T> currentStructures, DoubleIndex doubleIndex,
                                             List<Integer> innovationNumbers, int positionOfFitterParent,
                                             boolean includeConnectionsFromLessFitParent) {
        int positionOfYoungerStructure = determinePositionOfYoungerStructure(innovationNumbers);

        if (positionOfYoungerStructure == positionOfFitterParent || includeConnectionsFromLessFitParent) {
            T youngerStructure = currentStructures.get(positionOfYoungerStructure);
            offspringBuilder.addStructure(youngerStructure);
        }
        doubleIndex.increaseAt(positionOfYoungerStructure);
    }

    private int determinePositionOfYoungerStructure(List<Integer> currentInnovationNumbers) {
        Integer id0 = currentInnovationNumbers.get(0);
        Integer id1 = currentInnovationNumbers.get(1);

        if (id0 == null){
            return 1;
        } else if (id1 == null) {
            return 0;
        } else if (id0 < id1) {
            return 0;
        } else if (id1 < id0) {
            return 1;
        } else {
            throw new AssertionError("Both structures have the same innovation number.");
        }
    }

}
