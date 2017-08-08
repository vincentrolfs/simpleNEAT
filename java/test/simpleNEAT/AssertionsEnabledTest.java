package simpleNEAT;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertionsEnabledTest {

    @Test
    void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert(false) : "";
        });
    }

}
