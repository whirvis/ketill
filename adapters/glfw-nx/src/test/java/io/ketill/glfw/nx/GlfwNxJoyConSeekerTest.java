package io.ketill.glfw.nx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwNxJoyConSeekerTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new GlfwNxJoyConSeeker(0x00));
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwNxJoyConSeeker(0x01));

        /*
         * For the next tests to successfully execute, GLFW must
         * successfully initialize. If it fails to do so, that is
         * fine. It just means the current machine does not have
         * access to GLFW.
         */
        assumeTrue(glfwInit());

        /*
         * Any operating system running this test should pass,
         * assuming a valid GLFW window pointer is passed. If
         * an error occurs here, something was not configured
         * correctly. Likely, device GUIDs are missing for the
         * current operating system.
         */
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);
        assertDoesNotThrow(() -> new GlfwNxJoyConSeeker(ptr_glfwWindow));

        /*
         * It would not make sense to create a Nintendo Switch
         * Joy-Con seeker which does not seek either left or
         * right Joy-Cons. As such, assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwNxJoyConSeeker(ptr_glfwWindow, false, false));

        /* make sure to shut down GLFW */
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}