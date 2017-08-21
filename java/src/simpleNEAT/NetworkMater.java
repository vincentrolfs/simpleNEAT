package simpleNEAT;

import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkMater {

    NeuralNetwork createOffspring(NeuralNetwork parent0, NeuralNetwork parent1){
        LinkedList<Connection> offspringConnections = new LinkedList<>();

        List<List<Connection>> parentConnections = getParentsConnections(parent0, parent1);
        List<Integer> parentConnectionsSizes = getParentsConnectionSizes(parentConnections);

        MultiIndex multiIndex = new MultiIndex(2);

        Integer fitterParentNumber = determineFitterParent(parent0, parent1);
        boolean includeConnectionsFromLessFitParent = decideInclusionOfConnectionsFromLessFitParent();

        while (
                multiIndex.isSmallerEverywhere(parentConnectionsSizes)
        ) {
            List<Connection> connections = multiIndex.getValuesOf(parentConnections);
            List<Integer> innovationNumbers  = connections.stream()
                    .map(Connection::getInnovationNumber)
                    .collect(Collectors.toList());

            if (innovationNumbers.get(0).equals(innovationNumbers.get(1))){
                addMatchingConnection(offspringConnections, connections.get(0), connections.get(1));
                multiIndex.increaseAll();
                continue;
            }

            Integer youngerConnectionNumber = determineYoungerConnectionNumber(innovationNumbers);
            if (youngerConnectionNumber.equals(fitterParentNumber) || includeConnectionsFromLessFitParent){
                addNonMatchingConnection(offspringConnections, connections.get(youngerConnectionNumber));
            }
            multiIndex.increaseAt(youngerConnectionNumber);
        }

        false;
    }

    private void addNonMatchingConnection(LinkedList<Connection> offspringConnections, Connection connection) {
        false;
    }

    private Integer determineYoungerConnectionNumber(List<Integer> currentInnovationNumbers) {
        if (currentInnovationNumbers.get(0) < currentInnovationNumbers.get(1)){
            return 0;
        } else if (currentInnovationNumbers.get(0) > currentInnovationNumbers.get(1)){
            return 1;
        } else {
            assert false : "Both connection numbers are the same when they shouldn't be.";
        }
    }

    private void addMatchingConnection(LinkedList<Connection> offspringConnections, Connection connection, Connection connection1) {
        false;
    }

    private List<Integer> getParentsConnectionSizes(List<List<Connection>> parentConnections) {
        List<Integer> parentConnectionsSizes = new ArrayList<>();
        parentConnectionsSizes.add(parentConnections.get(0).size());
        parentConnectionsSizes.add(parentConnections.get(1).size());

        return parentConnectionsSizes;
    }

    private boolean decideInclusionOfConnectionsFromLessFitParent() {
        false;
    }

    private Integer determineFitterParent(NeuralNetwork parent0, NeuralNetwork parent1) {
        double fitness0 = parent0.getFitness();
        double fitness1 = parent1.getFitness();

        if (fitness0 > fitness1){
            return 0;
        } else if (fitness0 < fitness1){
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
