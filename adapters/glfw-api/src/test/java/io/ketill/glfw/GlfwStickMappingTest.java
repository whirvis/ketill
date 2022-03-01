package io.ketill.glfw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlfwStickMappingTest {

    @Test
    void __init__() {
        /*
         * It would not make sense to pass a negative index for the
         * axes or the Z-button (when specifying it, at least.) Any
         * negative value is guaranteed to result in an exception
         * when querying joystick axis or button data in GLFW. As
         * such, assume this was an error by the user.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwStickMapping(-1, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwStickMapping(0, -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwStickMapping(0, 0, -1));

        /*
         * When a Z-button is not specified, it is allowed to have
         * a negative value. It is expected of the adapter to make
         * sure a Z-button is present before querying for it.
         */
        GlfwStickMapping noZButton = new GlfwStickMapping(0, 0);
        assertTrue(noZButton.glfwZButton < 0);
        assertFalse(noZButton.hasZButton);
    }

}