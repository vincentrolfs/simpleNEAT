package simpleNEAT;

import simpleNEAT.NeuralNetwork.NeuralNetwork;

import java.util.HashSet;
import java.util.Set;

public class Species {
    private Set<NeuralNetwork> _networks;
    private NeuralNetwork _center;

    public Species(NeuralNetwork center) {
        _center = center;
        _networks = new HashSet<>();
    }

    void addNetwork(NeuralNetwork network){
        _networks.add(network);
    }

    public NeuralNetwork getCenter() {
        return _center;
    }

    public void setCenter(NeuralNetwork center) {
        _center = center;
    }
}
