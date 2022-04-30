package io.ketill.glfw;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwUtilsTest {

    @Test
    void requireWindow() {
        /*
         * It would not make sense for the equivalent of a NULL C pointer
         * to be passed for a GLFW window. Furthermore, should also not be
         * possible to pass an invalid non-null GLFW window pointer.
         */
        assertThrows(NullPointerException.class,
                () -> GlfwUtils.requireWindow(0x00));
        assertThrows(IllegalArgumentException.class,
                () -> GlfwUtils.requireWindow(0x01));

        /*
         * For the next tests to successfully execute, GLFW must initialize
         * successfully. If it fails to do so, that is fine. It just means
         * the current machine does not have access to GLFW.
         */
        assumeTrue(glfwInit());

        /*
         * Create a sample GLFW window and ensure requireWindow() returns
         * the same pointer that was passed in. This is done for the
         * convenience of the user.
         */
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long ptr_glfwWindow = glfwCreateWindow(1024, 768,
                "window", 0L, 0L);
        assertEquals(ptr_glfwWindow, GlfwUtils.requireWindow(ptr_glfwWindow));

        /* make sure to shut down GLFW */
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

    @Test
    void requireJoystick() {
        int last = GLFW_JOYSTICK_LAST;

        /*
         * No joystick outside GLFW's bounds should be allowed for use.
         * Attempting to use them would cause GLFW to throw an exception
         * when accessing joystick data.
         */
        assertThrows(IllegalArgumentException.class,
                () -> GlfwUtils.requireJoystick(last + 1));
        assertThrows(IllegalArgumentException.class,
                () -> GlfwUtils.requireJoystick(-1));

        /*
         * Any joystick within bounds of GLFW's  joysticks should be allowed.
         * Furthermore, requireJoystick() must return the same joystick that
         * was passed in. This is done for the convenience of the user.
         */
        int joystick = new Random().nextInt(last + 1);
        assertEquals(joystick, GlfwUtils.requireJoystick(joystick));
    }

}