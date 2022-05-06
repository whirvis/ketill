package io.ketill.glfw;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwUtilsTest {

    private static long ptr_glfwWindow;

    /*
     * For the next tests to successfully execute, GLFW must initialize
     * successfully. If it fails to do so, that is fine. It just means
     * the current machine does not have access to GLFW.
     */
    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    @Test
    void testRequireWindow() {
        /*
         * It would not make sense for the equivalent of a NULL C pointer
         * to be passed for a GLFW window. If an invalid non-null GLFW
         * window pointer is passed, requireWindow() will intentionally
         * crash the program. It is better to catch an invalid pointer
         * here than later down the line.
         */
        assertThrows(NullPointerException.class,
                () -> GlfwUtils.requireWindow(0x00));

        /*
         * For the convenience of the user, requireWindow() must return the
         * same pointer that was passed in.
         */
        assertEquals(ptr_glfwWindow, GlfwUtils.requireWindow(ptr_glfwWindow));
    }

    @Test
    void testRequireJoystick() {
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
         * Any joystick within bounds of GLFW's joysticks should be allowed.
         * Furthermore, requireJoystick() must return the same joystick that
         * was passed in. This is done for the convenience of the user.
         */
        int joystick = new Random().nextInt(last + 1);
        assertEquals(joystick, GlfwUtils.requireJoystick(joystick));
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}