package simpleNEAT.InnovationNumberObjectPairIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleNEAT.NetworkCreator;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InnovationNumberObjectPairIteratorTest {

    @Test
    void correspondingNeuronsAreLinedUpCorrectly() {
        NetworkCreator networkCreator = new NetworkCreator(
                1, 1,
                -0.5, 3, 1,
                7, 8, -4,
                0.2, 0.9, 0.5,
                3
        );
        NeuralNetwork network1 = networkCreator.createMinimalNeuralNetwork();
        NeuralNetwork network2 = networkCreator.createMinimalNeuralNetwork();
        List<Node> nodes1 = network1.getNodesSorted();
        List<Node> nodes2 = network2.getNodesSorted();
        InnovationNumberObjectPairIterator<Node> iterator = new InnovationNumberObjectPairIterator<>(nodes1, nodes2);

        Pair<Node> pair1 = iterator.next();
        assertEquals(nodes1.get(0), pair1.get(0));
        assertEquals(nodes2.get(0), pair1.get(1));
        Pair<Node> pair2 = iterator.next();
        assertEquals(nodes1.get(1), pair2.get(0));
        assertEquals(nodes2.get(1), pair2.get(1));

        assertFalse(iterator.hasNext());
    }

    @Test
    void disjointConnectionsArePresentedCorrectly() {
        NetworkCreator networkCreator = new NetworkCreator(
                5, 5,
                -0.5, 3, 1,
                7, 8, -4,
                0.2, 0.9, 0.5,
                3
        );
        NeuralNetwork network1 = networkCreator.createMinimalNeuralNetwork();
        NeuralNetwork network2 = networkCreator.createMinimalNeuralNetwork();
        List<Node> nodes1 = network1.getNodesSorted();
        List<Node> nodes2 = network2.getNodesSorted();

        Connection connectionA1 = networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(8).getInnovationNumber(), nodes1.get(9).getInnovationNumber()
        );
        Connection connectionA2 = networkCreator.createConnectionWithDefaultWeight(
                nodes2.get(8).getInnovationNumber(), nodes2.get(9).getInnovationNumber()
        );
        Connection connectionB1 = networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(0).getInnovationNumber(), nodes1.get(1).getInnovationNumber()
        );
        Connection connectionC2 = networkCreator.createConnectionWithDefaultWeight(
                nodes2.get(3).getInnovationNumber(), nodes2.get(3).getInnovationNumber()
        );
        Connection connectionD1 = networkCreator.createConnectionWithDefaultWeight(
                nodes1.get(0).getInnovationNumber(), nodes1.get(9).getInnovationNumber()
        );
        Connection connectionD2 = networkCreator.createConnectionWithDefaultWeight(
                nodes2.get(0).getInnovationNumber(), nodes2.get(9).getInnovationNumber()
        );

        network1.addConnection(connectionA1);
        network1.addConnection(connectionB1);
        network1.addConnection(connectionD1);

        network2.addConnection(connectionA2);
        network2.addConnection(connectionC2);
        network2.addConnection(connectionD2);

        InnovationNumberObjectPairIterator<Connection> iterator = new InnovationNumberObjectPairIterator<>(
                network1.getConnectionsSorted(), network2.getConnectionsSorted()
        );

        Pair<Connection> pair1 = iterator.next();
        assertEquals(connectionA1, pair1.get(0));
        assertEquals(connectionA2, pair1.get(1));

        Pair<Connection> pair2 = iterator.next();
        assertEquals(connectionB1, pair2.get(0));
        assertNull(pair2.get(1));

        Pair<Connection> pair3 = iterator.next();
        assertNull(pair3.get(0));
        assertEquals(connectionC2, pair3.get(1));

        Pair<Connection> pair4 = iterator.next();
        assertEquals(connectionD1, pair4.get(0));
        assertEquals(connectionD2, pair4.get(1));

        assertFalse(iterator.hasNext());
    }
}