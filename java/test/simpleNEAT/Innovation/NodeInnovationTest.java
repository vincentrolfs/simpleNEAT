package simpleNEAT.Innovation;

import org.junit.jupiter.api.Test;
import simpleNEAT.Innovation.NodeInnovation;

import static org.junit.jupiter.api.Assertions.*;

class NodeInnovationTest {
    @Test
    void sameConnectionSplitImpliesEquals() {
        NodeInnovation inn1 = new NodeInnovation(1123);
        NodeInnovation inn2 = new NodeInnovation(1123);

        assertTrue(inn1.equals(inn2));
        assertTrue(inn2.equals(inn1));
    }

    @Test
    void differentConnectionSplitImpliesNotEquals() {
        NodeInnovation inn1 = new NodeInnovation(1123);
        NodeInnovation inn2 = new NodeInnovation(1124);

        assertFalse(inn1.equals(inn2));
        assertFalse(inn2.equals(inn1));
    }

    @Test
    void sameConnectionSplitImpliesSameHashCode() {
        NodeInnovation inn1 = new NodeInnovation(1123);
        NodeInnovation inn2 = new NodeInnovation(1123);

        assertTrue(inn1.hashCode() == inn2.hashCode());
    }
}