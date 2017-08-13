package simpleNEAT.Innovation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionInnovationTest {
    @Test
    void sameNodesImpliesEquals() {
        ConnectionInnovation inn1 = new ConnectionInnovation(12,8342, 0);
        ConnectionInnovation inn2 = new ConnectionInnovation(13,8342, 0);

        assertTrue(inn1.equals(inn2));
        assertTrue(inn2.equals(inn1));
    }

    @Test
    void differentNodesImpliesNotEquals() {
        ConnectionInnovation inn1 = new ConnectionInnovation(11,8342, 13);
        ConnectionInnovation inn2 = new ConnectionInnovation(11,8342, 0);
        ConnectionInnovation inn3 = new ConnectionInnovation(199,922, 13);

        assertFalse(inn1.equals(inn2));
        assertFalse(inn2.equals(inn1));

        assertFalse(inn1.equals(inn3));
        assertFalse(inn3.equals(inn1));

        assertFalse(inn2.equals(inn3));
        assertFalse(inn3.equals(inn2));
    }

    @Test
    void sameNodesImpliesSameHashCode() {
        ConnectionInnovation inn1 = new ConnectionInnovation(90,328, 1386);
        ConnectionInnovation inn2 = new ConnectionInnovation(8712,328, 1386);

        assertTrue(inn1.hashCode() == inn2.hashCode());
    }
}