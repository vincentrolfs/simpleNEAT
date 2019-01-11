package simpleNEAT;

import simpleNEAT.NeuralNetwork.NeuralNetwork;


import java.util.*;

public class NetworkSpeciator {

    private final int _amountOfSpecies;
    private final NetworkComparator _comparator;

    public NetworkSpeciator(int amountOfSpecies, NetworkComparator comparator) {
        _amountOfSpecies = amountOfSpecies;
        _comparator = comparator;
    }

    Collection<Set<NeuralNetwork>> speciatePopulation(Set<NeuralNetwork> population) {
        assert population.size() >= _amountOfSpecies;

        Map<NeuralNetwork, Set<NeuralNetwork>> clusters;
        Set<NeuralNetwork> oldCenters;
        Set<NeuralNetwork> newCenters = getInitialCenters(population);

        do {
            clusters = initializeClusters(newCenters);
            assignNetworksToClusters(population, clusters);

            oldCenters = newCenters;
            newCenters = computeNewCenters(clusters);
        } while (!oldCenters.equals(newCenters));

        return clusters.values();
    }

    private Set<NeuralNetwork> getInitialCenters(Set<NeuralNetwork> population) {
        List<NeuralNetwork> populationList = new ArrayList<NeuralNetwork>(population);
        Set<NeuralNetwork> centers = RandomUtil.sampleMultipleFrom(populationList, _amountOfSpecies);

        return centers;
    }

    private Map<NeuralNetwork, Set<NeuralNetwork>> initializeClusters(Set<NeuralNetwork> newCenters) {
        Map<NeuralNetwork, Set<NeuralNetwork>> clusters = new HashMap<>();

        for (NeuralNetwork center : newCenters) {
            clusters.put(center, new HashSet<NeuralNetwork>());
        }

        return clusters;
    }

    private void assignNetworksToClusters(Set<NeuralNetwork> population, Map<NeuralNetwork, Set<NeuralNetwork>> clusters) {
        Set<NeuralNetwork> centers = clusters.keySet();

        for (NeuralNetwork network : population) {
            NeuralNetwork closestCenter = findClosestCenter(network, centers);
            clusters.get(closestCenter).add(network);
        }
    }

    private NeuralNetwork findClosestCenter(NeuralNetwork network, Set<NeuralNetwork> centers) {
        NeuralNetwork closestCenter = null;
        double smallestDistance = Double.MAX_VALUE;

        for (NeuralNetwork center : centers) {
            double distance = _comparator.calculateDistance(network, center);

            if (distance <= smallestDistance){
                closestCenter = center;
                smallestDistance = distance;
            }
        }

        return closestCenter;
    }

    private Set<NeuralNetwork> computeNewCenters(Map<NeuralNetwork, Set<NeuralNetwork>> clusters) {
        Set<NeuralNetwork> currentCenters = clusters.keySet();
        Set<NeuralNetwork> newCenters = new HashSet<>();

        for (NeuralNetwork center : currentCenters) {
            Set<NeuralNetwork> singleCluster = clusters.get(center);
            NeuralNetwork singleNewCenter = computeSingleNewCenter(singleCluster);
            newCenters.add(singleNewCenter);
        }

        return newCenters;
    }

    private NeuralNetwork computeSingleNewCenter(Set<NeuralNetwork> cluster) {
        NeuralNetwork newCenter = null;
        double smallestDistance = Double.MAX_VALUE;

        for (NeuralNetwork network : cluster) {
            double distance = calculateInterClusterDistance(network, cluster);

            if (distance <= smallestDistance){
                newCenter = network;
                smallestDistance = distance;
            }
        }

        return newCenter;
    }

    private double calculateInterClusterDistance(NeuralNetwork theNetwork, Set<NeuralNetwork> cluster) {
        double distance = 0;

        for (NeuralNetwork someNetwork : cluster) {
            distance += _comparator.calculateDistance(theNetwork, someNetwork);
        }

        return distance;
    }
}
