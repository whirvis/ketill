package io.ketill.glfw.nx;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwNxProSeekerTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    @Test
    void testInit() {
        /*
         * Any operating system running this test should pass, assuming
         * a valid GLFW window pointer is passed. If an error occurs here,
         * something was not configured correctly. Likely, device GUIDs
         * are missing for the current operating system.
         */
        assertDoesNotThrow(() -> new GlfwNxProSeeker(ptr_glfwWindow));
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}