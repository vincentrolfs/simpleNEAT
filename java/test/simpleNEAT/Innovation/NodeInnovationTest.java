package simpleNEAT.Innovation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeInnovationTest {
    @Test
    void sameConnectionSplitImpliesEquals() {
        NodeInnovation inn1 = new NodeInnovation(2,1123);
        NodeInnovation inn2 = new NodeInnovation(44,1123);

        assertTrue(inn1.equals(inn2));
        assertTrue(inn2.equals(inn1));
    }

    @Test
    void differentConnectionSplitImpliesNotEquals() {
        NodeInnovation inn1 = new NodeInnovation(66,1123);
        NodeInnovation inn2 = new NodeInnovation(66,1124);

        assertFalse(inn1.equals(inn2));
        assertFalse(inn2.equals(inn1));
    }

    @Test
    void sameConnectionSplitImpliesSameHashCode() {
        NodeInnovation inn1 = new NodeInnovation(122,1123);
        NodeInnovation inn2 = new NodeInnovation(0,1123);

        assertTrue(inn1.hashCode() == inn2.hashCode());
    }
}