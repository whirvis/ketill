package io.ketill.glfw;

import static org.lwjgl.glfw.GLFW.*;

class GlfwUtils {

    private static final long NULL_PTR = 0L;

    static long requireWindow(long ptr_glfwWindow) {
        if (ptr_glfwWindow == NULL_PTR) {
            throw new NullPointerException("null GLFW window pointer");
        }

        /*
         * If glfwGetWindowSize() sets X and Y to zero, it means an
         * error has occurred. When this occurs, it can be inferred
         * that the window pointer is not valid.
         */
        int[] x = new int[1], y = new int[1];
        glfwGetWindowSize(ptr_glfwWindow, x, y);
        if (x[0] == 0 || y[0] == 0) {
            throw new IllegalArgumentException("invalid GLFW window pointer");
        }

        return ptr_glfwWindow;
    }

    static int requireJoystick(int glfwJoystick) {
        if (glfwJoystick < 0 || glfwJoystick > GLFW_JOYSTICK_LAST) {
            throw new IllegalArgumentException("no such GLFW joystick");
        }
        return glfwJoystick;
    }

}
