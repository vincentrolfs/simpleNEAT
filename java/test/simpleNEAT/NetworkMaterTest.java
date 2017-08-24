package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMaterTest {

    NetworkMater _networkMater;
    NetworkCreator _networkCreator;
    NeuralNetwork _network1;
    NeuralNetwork _network2;

    @BeforeEach
    void setUp() {
        _networkMater = new NetworkMater(0.3);
        _networkCreator = new NetworkCreator(
                3,
                5,
                -1, 1, 0,
                -1, 1, 0.2,
                -1, 1, -0.1,
                3
        );
        _network1 = _networkCreator.createMinimalNeuralNetwork();
        _network2 = _networkCreator.createMinimalNeuralNetwork();

        _network1.setFitness(10d);
        _network2.setFitness(200d);
    }

    @RepeatedTest(100)
    void matesMinimalNetworksCorrectly() {
        List<Node> nodes1 = _network1.getNodes();
        List<Node> nodes2 = _network2.getNodes();

        nodes1.get(0).setBias(0.8);
        nodes2.get(0).setBias(0.8);

        nodes1.get(6).setActivationSteepness(-0.5);
        nodes2.get(6).setActivationSteepness(0.3);

        NeuralNetwork child = _networkMater.createOffspring(_network1, _network2);
        List<Connection> childConnections = child.getConnectionsSorted();
        List<Node> childNodes = child.getNodes();

        assertEquals(0, childConnections.size());
        assertEquals(8, childNodes.size());
        assertEquals(0.8, childNodes.get(0).getBias());

        double childActivationSteepness = childNodes.get(6).getActivationSteepness();
        System.out.println(childActivationSteepness);
        assertTrue(childActivationSteepness == -0.5 || childActivationSteepness == 0.3);

        for (int i = 0; i < 8; i++) {
            Node oneChildNode = childNodes.get(i);
            if (i != 0 && i != 6){
                assertEquals(0.2, oneChildNode.getBias());
                assertEquals(-0.1, oneChildNode.getActivationSteepness());
            }
        }
    }

    @Test
    void matesOneConnectionCorrectly() {
        int nodeOutOfId = _network1.getNodes().get(2).getInnovationNumber();
        int nodeIntoId = _network1.getNodes().get(6).getInnovationNumber();

        _network1.addConnection(_networkCreator.createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, 0.8));
        _network2.addConnection(_networkCreator.createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, 0.1));

        NeuralNetwork child = _networkMater.createOffspring(_network1, _network2);
        assertEquals(1, child.getConnectionsSorted().size());
        assertEquals(8, child.getNodes().size());

        double childConnectionWeight = child.getConnectionsSorted().get(0).getWeight();
        assertTrue(childConnectionWeight == 0.8 || childConnectionWeight == 0.1);

        List<Node> childNodes = child.getNodes();
        for (int i = 0; i < 8; i++) {
            Node oneChildNode = childNodes.get(i);

            assertEquals(0.2, oneChildNode.getBias());
            assertEquals(-0.1, oneChildNode.getActivationSteepness());
        }
    }

    @Test
    void chooseEachParentForMatchingConnections50PercentOfTheTime() {
        int nodeOutOfId = _network1.getNodes().get(2).getInnovationNumber();
        int nodeIntoId = _network1.getNodes().get(6).getInnovationNumber();

        _network1.addConnection(_networkCreator.createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, 0.8));
        _network2.addConnection(_networkCreator.createConnectionWithGivenWeight(nodeOutOfId, nodeIntoId, 0.1));

        int amountNetwork1Chosen = 0;

        for (int i = 0; i < 1000; i++) {
            NeuralNetwork child = _networkMater.createOffspring(_network1, _network2);
            Connection childConnection = child.getConnectionsSorted().get(0);
            if (childConnection.getWeight() == 0.8){
                amountNetwork1Chosen++;
            }
        }

        // 99,7% confidence interval.
        assertEquals(500, amountNetwork1Chosen, 48);
    }
}