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

    private NetworkMater _networkMater;
    private NetworkCreator _networkCreator;
    private NeuralNetwork _network1;
    private NeuralNetwork _network2;

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

    @Test
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
    void matesOneMatchingConnectionCorrectly() {
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
            } else {
                assertTrue(childConnection.getWeight() == 0.1);
            }
        }

        // 99,7% confidence interval.
        assertEquals(500, amountNetwork1Chosen, 48);
    }

    @Test
    void ifSpecifiedKeepsNonMatchingConnectionsOnlyFromFitterParent() {
        NetworkMater networkMater = new NetworkMater(0);
        List<Node> nodes1 = _network1.getNodes();
        List<Node> nodes2 = _network2.getNodes();

        Connection newConnectionA1 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(0).getInnovationNumber(),
                nodes1.get(4).getInnovationNumber(),
                0.12
        );

        Connection newConnectionB2 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(1).getInnovationNumber(),
                nodes1.get(5).getInnovationNumber(),
                -0.44
        );
        Connection newConnectionC2 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(3).getInnovationNumber(),
                nodes1.get(1).getInnovationNumber(),
                -1
        );

        _network1.addConnection(newConnectionA1);

        _network2.addConnection(newConnectionB2);
        _network2.addConnection(newConnectionC2);

        NeuralNetwork child = networkMater.createOffspring(_network1, _network2);
        List<Node> childNodes = child.getNodes();
        List<Connection> childConnections = child.getConnectionsSorted();

        assertEquals(8, childNodes.size());
        assertEquals(2, childConnections.size());

        assertEquals(newConnectionB2.getInnovationNumber(), childConnections.get(0).getInnovationNumber());
        assertEquals(newConnectionC2.getInnovationNumber(), childConnections.get(1).getInnovationNumber());

        assertEquals(-0.44, childConnections.get(0).getWeight());
        assertEquals(-1, childConnections.get(1).getWeight());
    }

    @Test
    void keepNonMatchingConnectionFromLessFitParentTheCorrectAmountOfTimes() {
        NetworkMater networkMater = new NetworkMater(0.68);
        List<Node> nodes1 = _network1.getNodes();
        List<Node> nodes2 = _network2.getNodes();

        Connection newConnection1 = _networkCreator.createConnectionWithRandomWeight(
                nodes1.get(4).getInnovationNumber(),
                nodes1.get(4).getInnovationNumber()
        );
        _network1.addConnection(newConnection1);

        Connection newConnection2 = _networkCreator.createConnectionWithRandomWeight(
                nodes2.get(4).getInnovationNumber(),
                nodes2.get(7).getInnovationNumber()
        );
        _network2.addConnection(newConnection2);

        int amountConnection1Kept = 0;

        for (int i = 0; i < 10000; i++){
            NeuralNetwork child = networkMater.createOffspring(_network1, _network2);
            int amountConnections = child.getConnectionsSorted().size();

            if (amountConnections == 2){
                amountConnection1Kept++;
            } else {
                assertEquals(1, amountConnections);
            }
        }

        // 99,7% confidence interval
        assertEquals(amountConnection1Kept, 6800, 140);
    }

    @Test
    void doesNotKeepDisconnectedNodes() {
        NetworkMater networkMater = new NetworkMater(0);
        List<Node> nodes1 = _network1.getNodes();
        List<Node> nodes2 = _network2.getNodes();

        Connection newConnectionA1 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(0).getInnovationNumber(),
                nodes1.get(4).getInnovationNumber(),
                0.12
        );
        Node newNode1 = _networkCreator.createNodeWithDefaultAttributes(newConnectionA1.getInnovationNumber());
        Connection newConnectionB1 = _networkCreator.createConnectionWithGivenWeight(
                newNode1.getInnovationNumber(),
                nodes1.get(4).getInnovationNumber(),
                -0.19
        );

        Connection newConnectionA2 = _networkCreator.createConnectionWithGivenWeight(
                nodes2.get(0).getInnovationNumber(),
                nodes2.get(4).getInnovationNumber(),
                0.12
        );

        _network1.addConnection(newConnectionA1);
        _network1.addNode(newNode1);
        _network1.addConnection(newConnectionB1);

        _network2.addConnection(newConnectionA2);

        NeuralNetwork child = networkMater.createOffspring(_network1, _network2);
        List<Node> childNodes = child.getNodes();
        List<Connection> childConnections = child.getConnectionsSorted();

        assertEquals(8, childNodes.size());
        assertEquals(1, childConnections.size());
        assertEquals(newConnectionA2.getInnovationNumber(), childConnections.get(0).getInnovationNumber());
    }

    @Test
    void keepsConnectedNodes() {
        NetworkMater networkMater = new NetworkMater(1);
        List<Node> nodes1 = _network1.getNodes();
        List<Node> nodes2 = _network2.getNodes();

        Connection newConnectionA1 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(2).getInnovationNumber(),
                nodes1.get(3).getInnovationNumber(),
                0.12
        );
        Node newNode1 = _networkCreator.createNodeWithDefaultAttributes(newConnectionA1.getInnovationNumber());
        Connection newConnectionB1 = _networkCreator.createConnectionWithGivenWeight(
                nodes1.get(2).getInnovationNumber(),
                newNode1.getInnovationNumber(),
                0.13
        );
        newNode1.setBias(0.111);
        _network1.addConnection(newConnectionA1);
        _network1.addNode(newNode1);
        _network1.addConnection(newConnectionB1);

        Connection newConnectionC2 = _networkCreator.createConnectionWithGivenWeight(
                nodes2.get(5).getInnovationNumber(),
                nodes2.get(3).getInnovationNumber(),
                0.16
        );
        Node newNode2 = _networkCreator.createNodeWithDefaultAttributes(newConnectionC2.getInnovationNumber());
        Connection newConnectionD2 = _networkCreator.createConnectionWithGivenWeight(
                newNode2.getInnovationNumber(),
                nodes2.get(3).getInnovationNumber(),
                0.17
        );
        newNode2.setActivationSteepness(0.222);
        _network2.addConnection(newConnectionC2);
        _network2.addNode(newNode2);
        _network2.addConnection(newConnectionD2);

        NeuralNetwork child = networkMater.createOffspring(_network1, _network2);
        List<Node> childNodes = child.getNodes();
        List<Connection> childConnections = child.getConnectionsSorted();

        assertEquals(10, childNodes.size());
        assertEquals(4, childConnections.size());

        assertEquals(newNode1.getInnovationNumber(), childNodes.get(8).getInnovationNumber());
        assertEquals(0.111, childNodes.get(8).getBias());
        assertNotEquals(newNode1, childNodes.get(8));

        assertEquals(newNode2.getInnovationNumber(), childNodes.get(9).getInnovationNumber());
        assertEquals(0.222, childNodes.get(9).getActivationSteepness());
        assertNotEquals(newNode2, childNodes.get(9));

    }
}