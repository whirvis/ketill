package io.ketill.glfw.psx;

import io.ketill.psx.PsxAmbiguityEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwPs4SeekerTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    private MockGlfwPs4Seeker seeker;

    @BeforeEach
    void createSeeker() {
        this.seeker = new MockGlfwPs4Seeker(ptr_glfwWindow);
    }

    @Test
    void testInit() {
        /*
         * Any operating system running this test should pass, assuming a
         * valid GLFW window pointer is passed. If an error occurs here,
         * something was not configured correctly. Likely, device GUIDs
         * are missing for the current operating system.
         */
        assertDoesNotThrow(() -> new GlfwPs4Seeker(ptr_glfwWindow));
    }

    @Test
    void testCheckAmbiguity() {
        AtomicBoolean ambiguous = new AtomicBoolean();
        seeker.subscribeEvents(PsxAmbiguityEvent.class, event -> {
            boolean nowAmbiguous = event.isNowAmbiguous();
            ambiguous.set(nowAmbiguous);
        });

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            /*
             * When only one controller is connected, the current PS4
             * controllers are not ambiguous. As such, isAmbiguous()
             * should return false and the event should not be emitted.
             */
            seeker.connectJoystick(glfw, GLFW_JOYSTICK_1);
            assertFalse(seeker.isAmbiguous());
            assertFalse(ambiguous.get());

            /*
             * Now that two controllers are connected, it is unknown if the
             * two PS4 controllers are the same physical device are not. As
             * such, isAmbiguous() should now return true and the event
             * should be emitted.
             */
            seeker.connectJoystick(glfw, GLFW_JOYSTICK_2);
            assertTrue(seeker.isAmbiguous());
            assertTrue(ambiguous.get());

            /*
             * Now that there is only one controller again, the current PS4
             * controllers are no longer ambiguous. Seeing as state the of
             * ambiguity has changed, the event should be emitted again.
             */
            seeker.disconnectJoystick(glfw, GLFW_JOYSTICK_2);
            assertFalse(seeker.isAmbiguous());
            assertFalse(ambiguous.get());
        }
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}