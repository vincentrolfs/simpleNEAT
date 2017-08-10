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

    @RepeatedTest(10)
    void randomConnectionWeightsAreDistributedEvenly() {
        int amountSmall = 0;

        for (int i = 0; i < 10000; i++) {
            double weight = _networkCreator.getRandomConnectionWeight();
            if (weight <= 1.25){
                amountSmall++;
            }
        }

        // 99,7%-confidence interval
        assertEquals(amountSmall, 5000, 150);
    }

    @Test
    void defaultNodeHasCorrectAttributes() {
        Node node = _networkCreator.createNodeWithDefaultAttributes(213);

        assertEquals(213, node.getInnovationNumber());
        assertEquals(0.5, node.getActivationSteepness());
        assertEquals(-4, node.getBias());
        assertFalse(node.isDisabled());
    }
}