package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class IoModeTest {

    @Test
    void testVariants() {
        /* This test only exists for coverage */
        assertNotNull(IoMode.READ);
        assertNotNull(IoMode.WRITE);
    }

}
