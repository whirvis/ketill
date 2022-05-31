package io.ketill.awt;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;

class AwtKeyMappingTest {

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(AwtKeyMapping.class).verify();
    }

    @Test
    void ensureImplementsToString() {
        AwtKeyMapping mapping = new AwtKeyMapping(0, 0);
        assertImplementsToString(AwtKeyMapping.class, mapping);
    }

}
