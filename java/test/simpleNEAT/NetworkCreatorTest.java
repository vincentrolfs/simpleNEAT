package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.Node;

import static org.junit.jupiter.api.Assertions.*;

class NetworkCreatorTest {

    // TODO: More tests
    
    private NetworkCreator _networkCreator;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(
                3, 4,
                -0.5, 3,
                7, 8, -4,
                0.2, 0.9, 0.5,
                3
        );
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
        Connection connection1 = _networkCreator.createNewConnection(2, 7);
        Connection connection2 = _networkCreator.createNewConnection(2, 0);
        Connection connection3 = _networkCreator.createNewConnection(2, 7);

        assertTrue(connection1.getInnovationNumber() < connection2.getInnovationNumber());
        assertTrue(connection1.getInnovationNumber() == connection3.getInnovationNumber());
    }

    @Test
    void sameConnectionInnovationGetsSameInnovationNumber() {
        Connection connection1 = _networkCreator.createNewConnection(2, 7);
        Connection connection2 = _networkCreator.createNewConnection(2, 0);
        Connection connection3 = _networkCreator.createNewConnection(2, 7);

        assertTrue(connection1.getInnovationNumber() == connection3.getInnovationNumber());
    }

    @Test
    void sameConnectionInnovationGetsSameInnovationNumberEvenInLaterGeneration() {
        Connection connection1 = _networkCreator.createNewConnection(223, 722);
        _networkCreator.nextGeneration();
        Connection connection2 = _networkCreator.createNewConnection(282, 3);
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        Connection connection3 = _networkCreator.createNewConnection(223, 722);

        assertTrue(connection1.getInnovationNumber() == connection3.getInnovationNumber());
    }

    @Test
    void networkCreatorForgetsAboutOldConnectionInnovations() {
        Connection connection1 = _networkCreator.createNewConnection(2, 7);
        _networkCreator.nextGeneration();
        Connection connection2 = _networkCreator.createNewConnection(2, 0);
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        _networkCreator.nextGeneration();
        Connection connection3 = _networkCreator.createNewConnection(2, 7);

        assertTrue(connection1.getInnovationNumber() < connection2.getInnovationNumber());
        assertTrue(connection2.getInnovationNumber() < connection3.getInnovationNumber());
    }
}