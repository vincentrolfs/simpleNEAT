package simpleNEAT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import simpleNEAT.NeuralNetwork.Node;

import static org.junit.jupiter.api.Assertions.*;

class NetworkCreatorTest {
    
    private NetworkCreator _networkCreator;

    @BeforeEach
    void setUp() {
        _networkCreator = new NetworkCreator(3, 4, -0.5, 
                3, 7, 8, -4, 0.2,
                0.9, 0.5);
    }

    @Test
    void randomConnectionWeightsAreInRange() {
        for (int i = 0; i < 10000; i++) {
            double weight = _networkCreator.getRandomConnectionWeight();
            assertTrue(-0.5 <= weight && weight <= 3);
        }
    }

    @Test
    void defaultNodeHasCorrectAttributes() {
        Node node = _networkCreator.createNodeWithDefaultAttributes(213);

        assertEquals(node.getInnovationNumber(), 213);
        assertEquals(node.getActivationSteepness(), 0.5);
        assertEquals(node.getBias(), -4);
        assertFalse(node.isDisabled());
    }
}