package io.ketill.glfw.nx;

import io.ketill.nx.NxLeftJoyCon;
import org.junit.jupiter.api.AfterEach;
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

    private long ptr_glfwWindow;
    private NxLeftJoyCon joycon;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);

        this.joycon = GlfwNxLeftJoyConAdapter.wrangle(ptr_glfwWindow,
                GLFW_JOYSTICK_1);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(joycon, NxLeftJoyCon.FEATURE_LED);
    }

    @Test
    void updateJoyConStick() {
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

            assertEquals(1.0F, joycon.ls.x());
            assertEquals(1.0F, joycon.ls.y());
            assertEquals(-1.0F, joycon.ls.z());

            /* update button states for next test */
            buttons.put(MAPPING_LS.glfwUp, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwDown, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwLeft, (byte) GLFW_PRESS);
            buttons.put(MAPPING_LS.glfwRight, (byte) GLFW_RELEASE);
            buttons.put(MAPPING_LS.glfwThumb, (byte) GLFW_RELEASE);

            joycon.poll(); /* update analog sticks */

            assertEquals(-1.0F, joycon.ls.x());
            assertEquals(-1.0F, joycon.ls.y());
            assertEquals(0.0F, joycon.ls.z());
        }
    }

    @Test
    void updateJoyConTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            ByteBuffer buttons = ByteBuffer.allocate(32);
            glfw.when(() -> glfwGetJoystickButtons(GLFW_JOYSTICK_1))
                    .thenReturn(buttons);

            buttons.put(ZL_INDEX, (byte) GLFW_PRESS);
            joycon.poll(); /* update analog triggers */
            assertEquals(1.0F, joycon.lt.getForce());

            buttons.put(ZL_INDEX, (byte) GLFW_RELEASE);
            joycon.poll(); /* update analog triggers */
            assertEquals(0.0F, joycon.lt.getForce());
        }
    }

    @AfterEach
    void shutdown() {
        if (ptr_glfwWindow != 0x00) {
            glfwDestroyWindow(ptr_glfwWindow);
            glfwTerminate();
        }
    }

}