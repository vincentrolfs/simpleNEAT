package simpleNEAT;

import simpleNEAT.NeuralNetwork.NeuralNetwork;

import java.util.Set;

public class Species {
    private Set<NeuralNetwork> _networks;

    public Species(Set<NeuralNetwork> networks) {
        _networks = networks;
    }

    void addNetwork(NeuralNetwork network){
        _networks.add(network);
    }
}
