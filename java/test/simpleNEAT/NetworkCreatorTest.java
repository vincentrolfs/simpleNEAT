package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkCreatorTest {

    private NetworkCreator _networkCreator;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(
                3, 4,
                -0.5, 3, 1,
                7, 8, -4,
                0.2, 0.9, 0.5,
                3
        );
    }

    @Test
    void createsMinimalNeuralNetworkCorrectly() {
        NeuralNetwork network = _networkCreator.createMinimalNeuralNetwork();
        List<Node> nodes = network.getNodes();
        List<Connection> connectionsSorted = network.getConnectionsSorted();

        assertEquals(3, network.getAmountInputNodes());
        assertEquals(4, network.getAmountOutputNodes());
        assertEquals(0, connectionsSorted.size());
        assertEquals(7, nodes.size());

        for (Node someNode : nodes){
            assertEquals(-4, someNode.getBias());
            assertEquals(0.5, someNode.getActivationSteepness());
        }
    }

    @Test
    void randomConnectionWeightsAreInRange() {
        for (int i = 0; i < 10000; i++) {
            double weight = _networkCreator.getRandomConnectionWeight();
            assertTrue(-0.5 <= weight && weight <= 3);
        }
    }

    @Test
    void newerConnectionGetsHigherInnovationNumber() {
        Connection connection1 = _networkCreator.createConnectionWithRandomWeight(2, 7);
        Connection connection2 = _networkCreator.createConnectionWithDefaultWeight(2, 0);

        assertTrue(connection1.getInnovationNumber() < connection2.getInnovationNumber());
    }

    @Test
    void newerNodeGetsHigherInnovationNumber() {
        Node node1 = _networkCreator.createNodeWithDefaultAttributes(32);
        Node node2 = _networkCreator.createNodeWithDefaultAttributes(0);

        assertTrue(node1.getInnovationNumber() < node2.getInnovationNumber());
    }

    @Test
    void sameConnectionInnovationGetsSameInnovationNumber() {
        Connection connection1 = _networkCreator.createConnectionWithGivenWeight(2, 7, 1.6);
        _networkCreator.createConnectionWithDefaultWeight(2, 0);
        _networkCreator.createNodeWithDefaultAttributes(7325);
        Connection connection3 = _networkCreator.createConnectionWithRandomWeight(2, 7);

        assertTrue(connection1.getInnovationNumber() == connection3.getInnovationNumber());
    }

    @Test
    void sameNodeInnovationGetsSameInnovationNumber() {
        Node node1 = _networkCreator.createNodeWithDefaultAttributes(237);
        _networkCreator.createNodeWithDefaultAttributes(3);
        _networkCreator.createNodeWithDefaultAttributes(2376);
        _networkCreator.createConnectionWithRandomWeight(2, 7);
        _networkCreator.createConnectionWithGivenWeight(2, 0, -0.2);
        _networkCreator.createConnectionWithRandomWeight(2, 7);
        Node node4 = _networkCreator.createNodeWithDefaultAttributes(237);

        assertTrue(node1.getInnovationNumber() == node4.getInnovationNumber());
    }

    @Test
    void sameConnectionInnovationGetsSameInnovationNumberEvenInLaterGeneration() {
        Connection connection1 = _networkCreator.createConnectionWithRandomWeight(223, 722);
        _networkCreator.nextGeneration();
        Connection connection2 = _networkCreator.createConnectionWithRandomWeight(282, 3);
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        Connection connection3 = _networkCreator.createConnectionWithRandomWeight(223, 722);

        assertTrue(connection1.getInnovationNumber() == connection3.getInnovationNumber());
    }

    @Test
    void sameNodeInnovationGetsSameInnovationNumberEvenInLaterGeneration() {
        Node node1 = _networkCreator.createNodeWithDefaultAttributes(32);
        _networkCreator.createNodeWithDefaultAttributes(333);

        _networkCreator.nextGeneration();

        _networkCreator.createNodeWithDefaultAttributes(32);
        _networkCreator.createConnectionWithGivenWeight(44, 0, -0.5);
        _networkCreator.createConnectionWithDefaultWeight(0, 0);

        _networkCreator.nextGeneration();

        _networkCreator.createConnectionWithRandomWeight(233, 7);
        Node node4 = _networkCreator.createNodeWithDefaultAttributes(32);

        assertTrue(node1.getInnovationNumber() == node4.getInnovationNumber());
    }

    @Test
    void networkCreatorForgetsAboutOldConnectionInnovations() {
        Connection connection1 = _networkCreator.createConnectionWithRandomWeight(2, 7);
        _networkCreator.nextGeneration();
        Connection connection2 = _networkCreator.createConnectionWithRandomWeight(2, 0);
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        Connection connection3 = _networkCreator.createConnectionWithRandomWeight(2, 7);

        assertTrue(connection1.getInnovationNumber() < connection2.getInnovationNumber());
        assertTrue(connection2.getInnovationNumber() < connection3.getInnovationNumber());
    }

    @Test
    void networkCreatorForgetsAboutOldNodeInnovations() {
        Node node1 = _networkCreator.createNodeWithDefaultAttributes(3244);
        _networkCreator.nextGeneration();
        Node node2 = _networkCreator.createNodeWithDefaultAttributes(32);
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        Node node3 = _networkCreator.createNodeWithDefaultAttributes(3244);

        assertTrue(node1.getInnovationNumber() < node2.getInnovationNumber());
        assertTrue(node2.getInnovationNumber() < node3.getInnovationNumber());
    }

    @Test
    void randomNewConnectionHasCorrectAttributes() {
        for (int i = 1; i <= 10000; i++) {
            Connection connection = _networkCreator.createConnectionWithRandomWeight(22, 12389);
            assertEquals(22, connection.getNodeOutOfId());
            assertEquals(12389, connection.getNodeIntoId());
            assertTrue(connection.getWeight() <= 3);
            assertTrue(connection.getWeight() >= -0.5);
            assertFalse(connection.isDisabled());
        }
    }

    @Test
    void newNodeWithDefaultAttributesHasCorrectAttributes() {
        for (int i = 1; i <= 10000; i++) {
            Node node = _networkCreator.createNodeWithDefaultAttributes(1273);
            assertEquals(0.5, node.getActivationSteepness());
            assertEquals(-4, node.getBias());
            assertFalse(node.isDisabled());
        }
    }

}