package io.ketill.glfw.nx;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwNxJoyConSeekerTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    private GlfwNxJoyConSeeker seeker;

    @BeforeEach
    void createSeeker() {
        this.seeker = new GlfwNxJoyConSeeker(ptr_glfwWindow);
    }

    @Test
    void testInit() {
        /*
         * It would not make sense to create a Nintendo Switch Joy-Con seeker
         * which does not seek either left or right Joy-Cons. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new GlfwNxJoyConSeeker(ptr_glfwWindow, false, false));

        /*
         * Any operating system running this test should pass, assuming
         * a valid GLFW window pointer is passed. If an error occurs here,
         * something was not configured correctly. Likely, device GUIDs
         * are missing for the current operating system.
         */
        assertDoesNotThrow(() -> new GlfwNxJoyConSeeker(ptr_glfwWindow));
    }

    @Test
    void testIsSeekingLeftJoyCons() {
        assertTrue(seeker.isSeekingLeftJoyCons());
    }

    @Test
    void testIsSeekingRightJoyCons() {
        assertTrue(seeker.isSeekingRightJoyCons());
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}