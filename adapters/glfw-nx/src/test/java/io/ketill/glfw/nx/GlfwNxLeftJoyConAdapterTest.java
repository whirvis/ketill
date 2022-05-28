package io.ketill.glfw.nx;

import io.ketill.nx.NxLeftJoyCon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.ByteBuffer;

import static io.ketill.KetillAssertions.*;
import static io.ketill.glfw.nx.GlfwNxLeftJoyConAdapter.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwNxLeftJoyConAdapterTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    private NxLeftJoyCon joycon;

    @BeforeEach
    void wrangleJoystick() {
        this.joycon = wrangle(ptr_glfwWindow, GLFW_JOYSTICK_1);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(joycon, NxLeftJoyCon.FEATURE_LED);
    }

    @Test
    void testUpdateJoyConStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            ByteBuffer buttons = ByteBuffer.allocate(32);
            glfw.when(() -> glfwGetJoystickButtons(GLFW_JOYSTICK_1))
                    .thenReturn(buttons);

            /* update button states for next test */
            buttons.put(MAPPING_LS.glfwUp, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwDown, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwLeft, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwRight, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwThumb, (byte) GLFW_PRESS);

            joycon.poll(); /* update analog sticks */

            assertEquals(1.0F, joycon.ls.getX());
            assertEquals(1.0F, joycon.ls.getY());
            assertEquals(-1.0F, joycon.ls.getZ());

            /* update button states for next test */
            buttons.put(MAPPING_LS.glfwUp, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwDown, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwLeft, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwRight, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwThumb, (byte) GLFW_RELEASE);

            joycon.poll(); /* update analog sticks */

            assertEquals(-1.0F, joycon.ls.getX());
            assertEquals(-1.0F, joycon.ls.getY());
            assertEquals(0.0F, joycon.ls.getZ());
        }
    }

    @Test
    void testUpdateJoyConTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            ByteBuffer buttons = ByteBuffer.allocate(32);
            glfw.when(() -> glfwGetJoystickButtons(GLFW_JOYSTICK_1))
                    .thenReturn(buttons);

            buttons.put(INDEX_ZL, (byte) GLFW_PRESS);
            joycon.poll(); /* update analog triggers */
            assertEquals(1.0F, joycon.lt.getForce());

            buttons.put(INDEX_ZL, (byte) GLFW_RELEASE);
            joycon.poll(); /* update analog triggers */
            assertEquals(0.0F, joycon.lt.getForce());
        }
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}