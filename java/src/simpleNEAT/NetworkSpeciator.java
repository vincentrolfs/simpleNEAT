package simpleNEAT;

import simpleNEAT.NeuralNetwork.NeuralNetwork;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NetworkSpeciator {

    private final int _amountOfSpecies;

    public NetworkSpeciator(int amountOfSpecies) {
        _amountOfSpecies = amountOfSpecies;
    }

    void speciatePopulation(Set<NeuralNetwork> population){
        assert population.size() >= _amountOfSpecies;

        Set<Species> allSpecies = createAllInitialSpecies(population);

        assert false;
        // TODO
    }

    private Set<Species> createAllInitialSpecies(Set<NeuralNetwork> population) {
        Set<Species> allInitialSpecies = new HashSet<>();
        Iterator<NeuralNetwork> iterator = population.iterator();
        int i = 1;

        while (iterator.hasNext() && i <= _amountOfSpecies){
            NeuralNetwork network = iterator.next();
            Species oneInitialSpecies = new Species(network);
            allInitialSpecies.add(oneInitialSpecies);
        }

        return allInitialSpecies;
    }

}
