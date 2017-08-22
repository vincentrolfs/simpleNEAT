package simpleNEAT;

import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkMater {

    private double _inclusionOfConnectionsFromLessFitParentProbability;

    public NetworkMater(double inclusionOfConnectionsFromLessFitParentProbability) {
        this._inclusionOfConnectionsFromLessFitParentProbability = inclusionOfConnectionsFromLessFitParentProbability;
    }

    NeuralNetwork createOffspring(NeuralNetwork parent0, NeuralNetwork parent1) {
        LinkedList<Connection> offspringConnectionsSorted = new LinkedList<>();

        List<List<Connection>> parentConnections = getParentsConnections(parent0, parent1);
        List<Integer> parentConnectionsSizes = getParentsConnectionSizes(parentConnections);

        MultiIndex multiIndex = new MultiIndex(2);

        Integer fitterParentNumber = determineFitterParent(parent0, parent1);
        boolean includeConnectionsFromLessFitParent = decideInclusionOfConnectionsFromLessFitParent();

        while (multiIndex.isSmallerEverywhere(parentConnectionsSizes)) {
            List<Connection> currentConnections = multiIndex.getValuesOf(parentConnections);
            List<Integer> innovationNumbers = getInnovationNumbers(currentConnections);

            if (innovationNumbers.get(0).equals(innovationNumbers.get(1))) {
                handleMatchingConnection(offspringConnectionsSorted, currentConnections, multiIndex);
            } else {
                handleNonMatchingConnection(offspringConnectionsSorted, currentConnections, innovationNumbers,
                        multiIndex, fitterParentNumber, includeConnectionsFromLessFitParent);
            }
        }

        handleRemainingConnections(offspringConnectionsSorted, parentConnections, parentConnectionsSizes, multiIndex,
                fitterParentNumber, includeConnectionsFromLessFitParent);

        return createOffspringFromConnections(offspringConnectionsSorted, parent0, parent1);
    }

    private void handleMatchingConnection(LinkedList<Connection> offspringConnections,
                                          List<Connection> currentConnections, MultiIndex multiIndex) {
        addMatchingConnection(offspringConnections, currentConnections);
        multiIndex.increaseAll();
    }

    private void handleNonMatchingConnection(LinkedList<Connection> offspringConnectionsSorted,
                                             List<Connection> currentConnections, List<Integer> innovationNumbers,
                                             MultiIndex multiIndex, Integer fitterParentNumber,
                                             boolean includeConnectionsFromLessFitParent) {
        Integer youngerConnectionNumber = determineYoungerConnectionNumber(innovationNumbers);

        if (youngerConnectionNumber.equals(fitterParentNumber) || includeConnectionsFromLessFitParent) {
            offspringConnectionsSorted.add(currentConnections.get(youngerConnectionNumber));
        }
        multiIndex.increaseAt(youngerConnectionNumber);
    }

    private void handleRemainingConnections(LinkedList<Connection> offspringConnectionsSorted,
                                            List<List<Connection>> parentConnections,
                                            List<Integer> parentConnectionsSizes, MultiIndex multiIndex,
                                            Integer fitterParentNumber, boolean includeConnectionsFromLessFitParent) {
        Integer parentWithRemainingConnectionsNumber = getNumberOfParentWithRemainingConnectons(
                parentConnectionsSizes, multiIndex
        );
    }

    private Integer getNumberOfParentWithRemainingConnectons(List<Integer> parentConnectionsSizes, MultiIndex multiIndex) {
        false;
    }

    private NeuralNetwork createOffspringFromConnections(LinkedList<Connection> offspringConnections, NeuralNetwork parent0, NeuralNetwork parent1) {
        false;
    }

    private List<Integer> getInnovationNumbers(List<Connection> connections) {
        return connections.stream()
                .map(Connection::getInnovationNumber)
                .collect(Collectors.toList());
    }

    private Integer determineYoungerConnectionNumber(List<Integer> currentInnovationNumbers) {
        if (currentInnovationNumbers.get(0) < currentInnovationNumbers.get(1)) {
            return 0;
        } else if (currentInnovationNumbers.get(0) > currentInnovationNumbers.get(1)) {
            return 1;
        } else {
            assert false : "Both connection numbers are the same when they shouldn't be.";
        }
    }

    private void addMatchingConnection(LinkedList<Connection> offspringConnections, List<Connection> currentConnections) {
        int selection = RandomUtil.generator.nextInt(2);
        offspringConnections.add(currentConnections.get(selection));
    }

    private List<Integer> getParentsConnectionSizes(List<List<Connection>> parentConnections) {
        List<Integer> parentConnectionsSizes = new ArrayList<>();
        parentConnectionsSizes.add(parentConnections.get(0).size());
        parentConnectionsSizes.add(parentConnections.get(1).size());

        return parentConnectionsSizes;
    }

    private boolean decideInclusionOfConnectionsFromLessFitParent() {
        return RandomUtil.getRandomBoolean(_inclusionOfConnectionsFromLessFitParentProbability);
    }

    private Integer determineFitterParent(NeuralNetwork parent0, NeuralNetwork parent1) {
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

    private List<Integer> getNewMultiindex() {
        List<Integer> multiindex = new ArrayList<>();
        multiindex.add(0);
        multiindex.add(0);

        return multiindex;
    }

    private List<List<Connection>> getParentsConnections(NeuralNetwork parent0, NeuralNetwork parent1) {
        List<List<Connection>> parentConnections = new ArrayList<>();
        parentConnections.add(parent0.getConnectionsSorted());
        parentConnections.add(parent1.getConnectionsSorted());

        return parentConnections;
    }

}
