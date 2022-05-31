package io.ketill.glfw;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

class GlfwStickMappingTest {

    @Test
    void testInit() {
        /*
         * It would not make sense to pass a negative index for the axes or
         * the Z-button (when specifying it, at least.) Any negative value is
         * guaranteed to result in an exception when querying joystick axis
         * or button data in GLFW. Assume this was an error by the user.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwStickMapping(-1, 0, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwStickMapping(0, -1, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> new GlfwStickMapping(0, 0, -1));

        /*
         * When a Z-button is not specified, they may have negative value.
         * It is expected of the adapter to ensure a Z-button is present
         * before querying its current state.
         */
        GlfwStickMapping noZButton = new GlfwStickMapping(0, 0);
        assertTrue(noZButton.glfwZButton < 0);
        assertFalse(noZButton.hasZButton);
    }

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(GlfwStickMapping.class).verify();
    }

    @Test
    void ensureImplementsToString() {
        GlfwStickMapping mapping = new GlfwStickMapping(0, 0);
        assertImplementsToString(GlfwStickMapping.class, mapping);
    }

}