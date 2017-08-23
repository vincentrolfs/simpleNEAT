package simpleNEAT;

import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.*;

public class NetworkMater {

    private double _inclusionOfConnectionsFromLessFitParentProbability;

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
                handleNonMatchingConnection(offspringBuilder, currentConnections, innovationNumbers,
                        multiIndex, fitterParentNumber, includeConnectionsFromLessFitParent);
            }
        }

        handleRemainingConnections(offspringBuilder,
                parentConnections, parentConnectionsSizes, multiIndex,
                fitterParentNumber, includeConnectionsFromLessFitParent);

        return false;
    }

    private class OffspringBuilder {
        NeuralNetwork _parent0;
        NeuralNetwork _parent1;

        LinkedList<Connection> _offspringConnections;
        ArrayList<Node> _offspringNodes;
        Set<Node> _alreadyAddedNodes;

        OffspringBuilder(NeuralNetwork parent0, NeuralNetwork parent1) {
            assert parent0.getAmountInputNodes() == parent1.getAmountInputNodes() &&
                    parent0.getAmountOutputNodes() == parent1.getAmountOutputNodes();

            _parent0 = parent0;
            _parent1 = parent1;

            _offspringConnections = new LinkedList<>();
            _offspringNodes = new ArrayList<>();
            _alreadyAddedNodes = new HashSet<Node>();

            addNonHiddenNodes();
        }

        void addConnection(Connection connection){
            _offspringConnections.add(connection);
            handleConnectedNodes(connection);
        }

        private void handleConnectedNodes(Connection connection){
            false;
        }

        private void addNonHiddenNodes(){
            int amountNonHiddenNodes = getAmountNonHiddenNodes();

            for (int nodeIndex = 0; nodeIndex < amountNonHiddenNodes; nodeIndex++) {
                addOneNonHiddenNode(nodeIndex);
            }
        }

        private void addOneNonHiddenNode(int nodeIndex) {
            NeuralNetwork selectedParent = getRandomParent();
            List<Node> allNodes = selectedParent.getNodes();
            Node selectedNode = allNodes.get(nodeIndex);

            addNode(selectedNode);
        }

        private int getAmountNonHiddenNodes() {
            int amountInputNodes = _parent0.getAmountInputNodes();
            int amountOutputNodes = _parent0.getAmountOutputNodes();

            return  amountInputNodes + amountOutputNodes;
        }

        private NeuralNetwork getRandomParent(){
            return RandomUtil.getRandomBoolean(0.5)? _parent0 : _parent1;
        }

        private void addNodeIfNotAddedAlready(Node node){
            if (!_alreadyAddedNodes.contains(node)){
                addNode(node);
            }
        }

        private void addNode(Node node){
            _offspringNodes.add(node);
            _alreadyAddedNodes.add(node);
        }
    }

    private void handleMatchingConnection(OffspringBuilder offspringBuilder,
                                          List<Connection> currentConnections, MultiIndex multiIndex) {
        Connection connection = getRandomConnection(currentConnections);
        offspringBuilder.addConnection(connection);
        multiIndex.increaseAll();
    }

    private void handleNonMatchingConnection(OffspringBuilder offspringBuilder,
                                             List<Connection> currentConnections, List<Integer> innovationNumbers,
                                             MultiIndex multiIndex, int fitterParentNumber,
                                             boolean includeConnectionsFromLessFitParent) {
        int youngerConnectionNumber = determineYoungerConnectionNumber(innovationNumbers);

        if (youngerConnectionNumber == fitterParentNumber || includeConnectionsFromLessFitParent) {
            Connection currentConnection = currentConnections.get(youngerConnectionNumber);
            offspringBuilder.addConnection(currentConnection);
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

        if (parentWithRemainingConnectionsNumber == null ||
                (!includeConnectionsFromLessFitParent && parentWithRemainingConnectionsNumber != fitterParentNumber)){
            return;
        }

        int index = multiIndex.get(parentWithRemainingConnectionsNumber);
        int size = parentConnectionsSizes.get(parentWithRemainingConnectionsNumber);
        List<Connection> connections = parentConnections.get(parentWithRemainingConnectionsNumber);

        for ( ; index < size; index++ ){
            Connection connection = connections.get(index);
            offspringBuilder.addConnection(connection);
        }
    }

    /**
     * Gets the number (0 or 1) of the parent that still has unseen connections remaining. At this stage there at most
     * one of the two parents has still unseen connections. Returns null if neither parent has unseen connections.
     */
    private Integer getParentWithRemainingConnectionsNumber(List<Integer> parentConnectionsSizes, MultiIndex multiIndex) {
        if (multiIndex.get(0) < parentConnectionsSizes.get(0)){
            return 0;
        } else if (multiIndex.get(1) < parentConnectionsSizes.get(1)){
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

    private Connection getRandomConnection(List<Connection> candidates) {
        int selection = RandomUtil.generator.nextInt(2);
        Connection connection = candidates.get(selection);

        return connection;
    }

    private boolean decideInclusionOfConnectionsFromLessFitParent() {
        return RandomUtil.getRandomBoolean(_inclusionOfConnectionsFromLessFitParentProbability);
    }

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
