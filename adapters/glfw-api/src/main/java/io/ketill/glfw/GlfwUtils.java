package io.ketill.glfw;

import static org.lwjgl.glfw.GLFW.*;

class GlfwUtils {

    private static final long NULL_PTR = 0L;
    private static final int[] WINDOW_SIZE = new int[1];

    static long requireWindow(long ptr_glfwWindow) {
        if (ptr_glfwWindow == NULL_PTR) {
            String msg = "GLFW window pointer cannot be NULL";
            throw new NullPointerException(msg);
        }

        /*
         * Attempt to grab the window size. If an invalid pointer was passed,
         * this will likely cause the program to crash. This is good, as it
         * ensures the program will crash sooner than later (in turn, making
         * finding the bug easier.)
         */
        glfwGetWindowSize(ptr_glfwWindow, WINDOW_SIZE, WINDOW_SIZE);

        return ptr_glfwWindow;
    }

    static int requireJoystick(int glfwJoystick) {
        if (glfwJoystick < 0 || glfwJoystick > GLFW_JOYSTICK_LAST) {
            throw new IllegalArgumentException("no such GLFW joystick");
        }
        return glfwJoystick;
    }

}
