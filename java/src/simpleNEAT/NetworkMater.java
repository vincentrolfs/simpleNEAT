package simpleNEAT;

import simpleNEAT.InnovationNumberObjectPairIterator.InnovationNumberObjectPairIterator;
import simpleNEAT.InnovationNumberObjectPairIterator.Pair;
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

        NeuralNetwork build(int amountInputNodes, int amountOutputNodes) {
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
        <T> void addStructure(T structure) {
            if (structure instanceof Node) {
                addNode(structure);
            } else if (structure instanceof Connection) {
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
            _indices = new int[]{0, 0};
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
         *
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
         *
         * @param position Must between 0 and 1 inclusive.
         */
        void increaseAt(int position) {
            _indices[position]++;
        }

        private <T> T tryGetSingleValue(List<T> list, int index) {
            if (index < list.size()) {
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
        assert inclusionOfConnectionsFromLessFitParentProbability >= 0 &&
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
        InnovationNumberObjectPairIterator<T> iterator = new InnovationNumberObjectPairIterator<>(structures0, structures1);

        while (iterator.hasNext()) {
            Pair<T> currentStructures = iterator.next();

            if (currentStructures.hasNullElement()) {
                handleNonMatchingStructure(offspringBuilder, currentStructures, positionOfFitterParent,
                        includeStructuresFromLessFitParent);
            } else {
                handleMatchingStructure(offspringBuilder, currentStructures);
            }
        }
    }

    private <T> void handleNonMatchingStructure(OffspringBuilder offspringBuilder, Pair<T> currentStructures,
                                                int positionOfFitterParent,
                                                boolean includeConnectionsFromLessFitParent) {
        int positionOfStructure = currentStructures.indexOfUniqueNonNullElement();
        T structure = currentStructures.get(positionOfStructure);

        if (positionOfStructure == positionOfFitterParent || includeConnectionsFromLessFitParent) {
            offspringBuilder.addStructure(structure);
        }
    }

    private <T> void handleMatchingStructure(OffspringBuilder offspringBuilder, Pair<T> currentStructures) {
        T structure = RandomUtil.sampleFrom(currentStructures.toList());
        offspringBuilder.addStructure(structure);
    }

}
