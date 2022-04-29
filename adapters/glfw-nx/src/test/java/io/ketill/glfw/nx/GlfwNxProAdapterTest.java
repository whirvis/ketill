package io.ketill.glfw.nx;

import io.ketill.nx.NxProController;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwNxProAdapterTest {

    private static final Random RANDOM = new Random();

    private long ptr_glfwWindow;
    private int glfwJoystick;
    private NxProController controller;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);
        this.glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);

        this.controller = GlfwNxProAdapter.wrangle(ptr_glfwWindow,
                glfwJoystick);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(controller, NxProController.FEATURE_LED);
    }

    @Test
    void updateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(8);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            /* update LS axes for next test */
            Vector3f ls = new Vector3f(RANDOM.nextFloat(),
                    RANDOM.nextFloat(), 0.00F);
            axes.put(GlfwNxProAdapter.MAPPING_LS.glfwXAxis, ls.x);
            axes.put(GlfwNxProAdapter.MAPPING_LS.glfwYAxis, ls.y);

            controller.poll(); /* update sticks */

            ls.y *= -1.0F;
            controller.calibration.applyLs(ls);
            assertEquals(ls.x, controller.ls.x());
            assertEquals(ls.y, controller.ls.y());

            /* update RS axes for next test */
            Vector3f rs = new Vector3f(RANDOM.nextFloat(),
                    RANDOM.nextFloat(), 0.00F);
            axes.put(GlfwNxProAdapter.MAPPING_RS.glfwXAxis, rs.x);
            axes.put(GlfwNxProAdapter.MAPPING_RS.glfwYAxis, rs.y);

            controller.poll(); /* update sticks */

            rs.y *= -1.0F;
            controller.calibration.applyRs(rs);
            assertEquals(rs.x, controller.rs.x());
            assertEquals(rs.y, controller.rs.y());
        }
    }

    @Test
    void updateProTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            ByteBuffer buttons = ByteBuffer.allocate(32);
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);

            /* update button states for next test */
            buttons.put(GlfwNxProAdapter.ZL_INDEX, (byte) GLFW_PRESS);
            buttons.put(GlfwNxProAdapter.ZR_INDEX, (byte) GLFW_PRESS);

            controller.poll(); /* update triggers */
            assertEquals(1.0F, controller.lt.getForce());
            assertEquals(1.0F, controller.rt.getForce());

            /* update button states for next test */
            buttons.put(GlfwNxProAdapter.ZL_INDEX, (byte) GLFW_RELEASE);
            buttons.put(GlfwNxProAdapter.ZR_INDEX, (byte) GLFW_RELEASE);

            controller.poll(); /* update triggers */
            assertEquals(0.0F, controller.lt.getForce());
            assertEquals(0.0F, controller.rt.getForce());
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