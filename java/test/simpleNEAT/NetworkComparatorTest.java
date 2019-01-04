package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkComparatorTest {
    private NetworkCreator _networkCreator;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(
                3, 4,
                -100, 100, -0.5,
                -100, 100, -4,
                -100, 100, 0.5,
                3
        );
    }

    @Test
    void distanceBetweenTheSameNetworksIsZero() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        NetworkComparator comparator = new NetworkComparator(
                1,1,1,
                1,1
        );

        assertEquals(0, comparator.calculateDistance(network, network));
    }

    @Test
    void countsDisjointsCorrectly() {
        NeuralNetwork network1 = _networkCreator.createMinimalNeuralNetwork();
        NeuralNetwork network2 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> nodes1 = network1.getNodesSorted();
        List<Node> nodes2 = network2.getNodesSorted();

        Connection sharedConnection = _networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(0).getInnovationNumber(),
                nodes1.get(1).getInnovationNumber()
        );
        Connection disjointConnection1 = _networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(3).getInnovationNumber(),
                nodes1.get(4).getInnovationNumber()
        );
        Connection disjointConnection2 = _networkCreator.createConnectionWithDefaultWeight(
                nodes2.get(5).getInnovationNumber(),
                nodes2.get(6).getInnovationNumber()
        );
        Node disjointNode1 = _networkCreator.createNodeWithDefaultAttributes(disjointConnection1.getInnovationNumber());

        network1.addConnection(sharedConnection);
        network1.addConnection(disjointConnection1);
        network1.addNode(disjointNode1);

        network2.addConnection(sharedConnection);
        network2.addConnection(disjointConnection2);

        NetworkComparator comparator = new NetworkComparator(
                0,0,0,
                39,1
        );

        assertEquals(41, comparator.calculateDistance(network1, network2));
    }

    @Test
    void comparesFullExampleCorrectly() {
        NeuralNetwork network1 = _networkCreator.createMinimalNeuralNetwork();
        NeuralNetwork network2 = _networkCreator.createMinimalNeuralNetwork();
        List<Node> nodes1 = network1.getNodesSorted();
        List<Node> nodes2 = network2.getNodesSorted();

        Connection sharedConnection1 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(0).getInnovationNumber(),
                nodes1.get(1).getInnovationNumber(),
                10
        );
        Connection sharedConnection2 = _networkCreator.createConnectionWithGivenWeight(
                nodes2.get(0).getInnovationNumber(),
                nodes2.get(1).getInnovationNumber(),
                27
        );
        Connection disjointConnection1 = _networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(3).getInnovationNumber(),
                nodes1.get(4).getInnovationNumber()
        );
        Connection disjointConnection2 = _networkCreator.createConnectionWithDefaultWeight(
                nodes2.get(5).getInnovationNumber(),
                nodes2.get(6).getInnovationNumber()
        );
        Node sharedNode1 = _networkCreator.createNodeWithDefaultAttributes(sharedConnection1.getInnovationNumber());
        Node sharedNode2 = _networkCreator.createNodeWithDefaultAttributes(sharedConnection2.getInnovationNumber());
        Node disjointNode1 = _networkCreator.createNodeWithDefaultAttributes(disjointConnection1.getInnovationNumber());

        sharedNode1.setBias(-19);
        sharedNode1.setActivationSteepness(-20);

        sharedNode2.setBias(0);
        sharedNode2.setActivationSteepness(3);

        network1.addConnection(sharedConnection1);
        network1.addConnection(disjointConnection1);
        network1.addNode(sharedNode1);
        network1.addNode(disjointNode1);

        network2.addConnection(sharedConnection2);
        network2.addConnection(disjointConnection2);
        network2.addNode(sharedNode2);

        NetworkComparator comparator = new NetworkComparator(
                3,5,7,
                11,13
        );

        assertEquals(320, comparator.calculateDistance(network1, network2));
    }
}