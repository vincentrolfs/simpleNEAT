package simpleNEAT;

import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.*;

public class NetworkMater {

    private double _inclusionOfConnectionsFromLessFitParentProbability;

    private class OffspringBuilder {
        NeuralNetwork _parent0;
        NeuralNetwork _parent1;

        LinkedList<Connection> _offspringConnections;
        ArrayList<Node> _offspringNodes;
        Set<Integer> _alreadyAddedNodeInnovationNumbers;

        OffspringBuilder(NeuralNetwork parent0, NeuralNetwork parent1) {
            assert parent0.getAmountInputNodes() == parent1.getAmountInputNodes() &&
                    parent0.getAmountOutputNodes() == parent1.getAmountOutputNodes();

            _parent0 = parent0;
            _parent1 = parent1;

            _offspringConnections = new LinkedList<>();
            _offspringNodes = new ArrayList<>();
            _alreadyAddedNodeInnovationNumbers = new HashSet<>();

            addNonHiddenNodes();
        }

        NeuralNetwork build(){
            return new NeuralNetwork(
                    _offspringNodes,
                    _offspringConnections,
                    _parent0.getAmountInputNodes(),
                    _parent0.getAmountOutputNodes()
            );
        }

        /**
         * Adds connection and needed nodes to the builder. Must be called in order of innovation numbers of
         * the connections!
         */
        void addConnection(Connection connection){
            _offspringConnections.add(connection);
            addNodeByIdIfNotAddedAlready(connection.getNodeOutOfId());
            addNodeByIdIfNotAddedAlready(connection.getNodeIntoId());
        }

        private void addNodeByIdIfNotAddedAlready(int innovationNumber){
            NeuralNetwork selectedParent = determineParentForNode(innovationNumber);
            Node selectedNode = selectedParent.getNodeByInnovationNumber(innovationNumber);

            addNodeIfIdNotAddedAlready(selectedNode);
        }

        private NeuralNetwork determineParentForNode(int innovationNumber) {
            if (!_parent0.isNodeIdInNetwork(innovationNumber)){
                return _parent1;
            } else if (!_parent1.isNodeIdInNetwork(innovationNumber)){
                return _parent0;
            } else {
                return getRandomParent();
            }
        }

        private void addNonHiddenNodes(){
            int amountNonHiddenNodes = getAmountNonHiddenNodes();

            for (int nodeIndex = 0; nodeIndex < amountNonHiddenNodes; nodeIndex++) {
                addOneNonHiddenNode(nodeIndex);
            }
        }

        private int getAmountNonHiddenNodes() {
            int amountInputNodes = _parent0.getAmountInputNodes();
            int amountOutputNodes = _parent0.getAmountOutputNodes();

            return  amountInputNodes + amountOutputNodes;
        }

        private void addOneNonHiddenNode(int nodeIndex) {
            NeuralNetwork selectedParent = getRandomParent();
            List<Node> allNodes = selectedParent.getNodes();
            Node selectedNode = allNodes.get(nodeIndex);

            forceAddNode(selectedNode);
        }

        private NeuralNetwork getRandomParent(){
            return RandomUtil.getRandomBoolean(0.5)? _parent0 : _parent1;
        }

        private void addNodeIfIdNotAddedAlready(Node node){
            int innovationNumber = node.getInnovationNumber();

            if (!_alreadyAddedNodeInnovationNumbers.contains(innovationNumber)){
                forceAddNode(node);
            }
        }

        private void forceAddNode(Node node){
            _offspringNodes.add(node);
            _alreadyAddedNodeInnovationNumbers.add(node.getInnovationNumber());
        }
    }

    public NetworkMater(double inclusionOfConnectionsFromLessFitParentProbability) {
        this._inclusionOfConnectionsFromLessFitParentProbability = inclusionOfConnectionsFromLessFitParentProbability;
    }

    NeuralNetwork createOffspring(NeuralNetwork parent0, NeuralNetwork parent1) {
        OffspringBuilder offspringBuilder = new OffspringBuilder(parent0, parent1);

        List<List<Connection>> parentConnections = getParentConnections(parent0, parent1);
        List<Integer> parentConnectionsSizes = getParentConnectionsSizes(parentConnections);

        MultiIndex multiIndex = new MultiIndex(2);

        int fitterParentNumber = determineFitterParent(parent0, parent1);
        boolean includeConnectionsFromLessFitParent = decideInclusionOfConnectionsFromLessFitParent();

        while (multiIndex.isSmallerEverywhere(parentConnectionsSizes)) {
            List<Connection> currentConnections = multiIndex.getValuesOf(parentConnections);
            List<Integer> innovationNumbers = getInnovationNumbers(currentConnections);

            if (innovationNumbers.get(0).equals(innovationNumbers.get(1))) {
                handleMatchingConnection(offspringBuilder, currentConnections, multiIndex);
            } else {
                handleNonMatchingConnection(offspringBuilder, currentConnections, multiIndex,
                         innovationNumbers, fitterParentNumber, includeConnectionsFromLessFitParent);
            }
        }

        handleRemainingConnections(offspringBuilder,
                parentConnections, parentConnectionsSizes, multiIndex,
                fitterParentNumber, includeConnectionsFromLessFitParent);

        return offspringBuilder.build();
    }

    private void handleMatchingConnection(OffspringBuilder offspringBuilder,
                                          List<Connection> currentConnections, MultiIndex multiIndex) {
        Connection connection = RandomUtil.sampleFrom(currentConnections);
        offspringBuilder.addConnection(connection);
        multiIndex.increaseAll();
    }

    private void handleNonMatchingConnection(OffspringBuilder offspringBuilder,
                                             List<Connection> currentConnections, MultiIndex multiIndex,
                                             List<Integer> innovationNumbers, int fitterParentNumber,
                                             boolean includeConnectionsFromLessFitParent) {
        int youngerConnectionNumber = determineYoungerConnectionNumber(innovationNumbers);

        if (youngerConnectionNumber == fitterParentNumber || includeConnectionsFromLessFitParent) {
            Connection youngerConnection = currentConnections.get(youngerConnectionNumber);
            offspringBuilder.addConnection(youngerConnection);
        }
        multiIndex.increaseAt(youngerConnectionNumber);
    }

    private void handleRemainingConnections(OffspringBuilder offspringBuilder,
                                            List<List<Connection>> parentConnections,
                                            List<Integer> parentConnectionsSizes, MultiIndex multiIndex,
                                            int fitterParentNumber, boolean includeConnectionsFromLessFitParent) {
        Integer parentWithRemainingConnectionsNumber = getParentWithRemainingConnectionsNumber(
                parentConnectionsSizes, multiIndex
        );

        if (parentWithRemainingConnectionsNumber == null) {
            return;
        }

        if (parentWithRemainingConnectionsNumber != fitterParentNumber && !includeConnectionsFromLessFitParent){
            return;
        }

        int index = multiIndex.getOneIndex(parentWithRemainingConnectionsNumber);
        int size = parentConnectionsSizes.get(parentWithRemainingConnectionsNumber);
        List<Connection> connections = parentConnections.get(parentWithRemainingConnectionsNumber);

        for ( ; index < size; index++ ){
            Connection connection = connections.get(index);
            offspringBuilder.addConnection(connection);
        }
    }

    /**
     * Gets the number (0 or 1) of the parent that still has unseen connections remaining. At this stage at most
     * one of the two parents has still unseen connections. Returns null if neither parent has unseen connections.
     */
    private Integer getParentWithRemainingConnectionsNumber(List<Integer> parentConnectionsSizes, MultiIndex multiIndex) {
        boolean parent0HasRemainingConnections = (multiIndex.getOneIndex(0) < parentConnectionsSizes.get(0));
        boolean parent1HasRemainingConnections = (multiIndex.getOneIndex(1) < parentConnectionsSizes.get(1));

        assert !(parent0HasRemainingConnections && parent1HasRemainingConnections);

        if (parent0HasRemainingConnections){
            return 0;
        } else if (parent1HasRemainingConnections){
            return 1;
        } else {
            return null;
        }
    }

    private List<Integer> getInnovationNumbers(List<Connection> connections) {
        List<Integer> innovationNumbers = new ArrayList<>();
        innovationNumbers.add(connections.get(0).getInnovationNumber());
        innovationNumbers.add(connections.get(1).getInnovationNumber());

        return  innovationNumbers;
    }

    private int determineYoungerConnectionNumber(List<Integer> currentInnovationNumbers) {
        if (currentInnovationNumbers.get(0) < currentInnovationNumbers.get(1)) {
            return 0;
        } else if (currentInnovationNumbers.get(0) > currentInnovationNumbers.get(1)) {
            return 1;
        } else {
            throw new AssertionError("Both connection numbers are the same but they shouldn't be.");
        }
    }

    private boolean decideInclusionOfConnectionsFromLessFitParent() {
        return RandomUtil.getRandomBoolean(_inclusionOfConnectionsFromLessFitParentProbability);
    }

    /**
     * Returns 0 if the first parent is fitter, 1 if the second parent is fitter, and a random value of either 0 or 1 if
     * they have the same fitness.
     */
    private int determineFitterParent(NeuralNetwork parent0, NeuralNetwork parent1) {
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

    private List<List<Connection>> getParentConnections(NeuralNetwork parent0, NeuralNetwork parent1) {
        List<List<Connection>> parentConnections = new ArrayList<>();
        parentConnections.add(parent0.getConnectionsSorted());
        parentConnections.add(parent1.getConnectionsSorted());

        return parentConnections;
    }

    private List<Integer> getParentConnectionsSizes(List<List<Connection>> parentConnections) {
        List<Integer> parentConnectionsSizes = new ArrayList<>();
        parentConnectionsSizes.add(parentConnections.get(0).size());
        parentConnectionsSizes.add(parentConnections.get(1).size());

        return parentConnectionsSizes;
    }

}
