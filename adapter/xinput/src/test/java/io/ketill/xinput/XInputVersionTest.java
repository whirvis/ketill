package io.ketill.xinput;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
class XInputVersionTest {

    /* @formatter:off */
    private static final @NotNull XInputVersion
            V1_0 = XInputVersion.V1_0,
            V1_3 = XInputVersion.V1_3,
            V1_4 = XInputVersion.V1_4;
    /* @formatter:on */

    @Test
    void testIsAtLeast() {
        /*
         * It makes no sense to check if a version is at least another
         * null version. As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> V1_0.isAtLeast(null));

        assertTrue(V1_0.isAtLeast(V1_0));
        assertFalse(V1_0.isAtLeast(V1_3));
        assertFalse(V1_0.isAtLeast(V1_4));

        assertTrue(V1_3.isAtLeast(V1_0));
        assertTrue(V1_3.isAtLeast(V1_3));
        assertFalse(V1_3.isAtLeast(V1_4));

        assertTrue(V1_4.isAtLeast(V1_0));
        assertTrue(V1_4.isAtLeast(V1_3));
        assertTrue(V1_4.isAtLeast(V1_4));
    }

    @Test
    void testGetDescription() {
        assertNotNull(V1_0.getDescription());
        assertNotNull(V1_3.getDescription());
        assertNotNull(V1_4.getDescription());
    }

}
