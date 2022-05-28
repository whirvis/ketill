package io.ketill.glfw;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("ConstantConditions")
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

    @Test
    void testRequireButton() {
        /*
         * It would not make sense to check a parameter with a name that is
         * null, empty, or surrounded by whitespace. It would also not make
         * sense to have a negative button count. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> GlfwUtils.requireButton(0, null));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireButton(0, ""));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireButton(0, " "));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireButton(0, -1));

        /*
         * When an invalid GLFW button is passed to this method, it should
         * throw an IndexOutOfBoundsException to the caller.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireButton(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireButton(1, 0));

        /*
         * When a valid GLFW button is passed to this method, it should
         * return the same value passed to it. This is to follow the
         * pattern of Objects.requireNonNull().
         */
        assertEquals(0, GlfwUtils.requireButton(0, 4, "glfwButton"));
        assertEquals(1, GlfwUtils.requireButton(1, "glfwButton"));
        assertEquals(2, GlfwUtils.requireButton(2, 4));
        assertEquals(3, GlfwUtils.requireButton(3));
    }

    @Test
    void testRequireAxis() {
        /*
         * It would not make sense to check a parameter with a name that is
         * null, empty, or surrounded by whitespace. It would also not make
         * sense to have a negative axis count. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> GlfwUtils.requireAxis(0, null));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireAxis(0, ""));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireAxis(0, " "));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireAxis(0, -1));

        /*
         * When an invalid GLFW axis is passed to this method, it should
         * throw an IndexOutOfBoundsException to the caller.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireAxis(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> GlfwUtils.requireAxis(1, 0));

        /*
         * When a valid GLFW axis is passed to this method, it should
         * return the same value passed to it. This is to follow the
         * pattern of Objects.requireNonNull().
         */
        assertEquals(0, GlfwUtils.requireAxis(0, 4, "glfwAxis"));
        assertEquals(1, GlfwUtils.requireAxis(1, "glfwAxis"));
        assertEquals(2, GlfwUtils.requireAxis(2, 4));
        assertEquals(3, GlfwUtils.requireAxis(3));
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}