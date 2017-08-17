package simpleNEAT.Innovation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HiddenNodeInnovationTest {
    @Test
    void sameConnectionSplitImpliesEquals() {
        HiddenNodeInnovation inn1 = new HiddenNodeInnovation(2,1123);
        HiddenNodeInnovation inn2 = new HiddenNodeInnovation(44,1123);

        assertTrue(inn1.equals(inn2));
        assertTrue(inn2.equals(inn1));
    }

    @Test
    void differentConnectionSplitImpliesNotEquals() {
        HiddenNodeInnovation inn1 = new HiddenNodeInnovation(66,1123);
        HiddenNodeInnovation inn2 = new HiddenNodeInnovation(66,1124);

        assertFalse(inn1.equals(inn2));
        assertFalse(inn2.equals(inn1));
    }

    @Test
    void sameConnectionSplitImpliesSameHashCode() {
        HiddenNodeInnovation inn1 = new HiddenNodeInnovation(122,1123);
        HiddenNodeInnovation inn2 = new HiddenNodeInnovation(0,1123);

        assertTrue(inn1.hashCode() == inn2.hashCode());
    }
}