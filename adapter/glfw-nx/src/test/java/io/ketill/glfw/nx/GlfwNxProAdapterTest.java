package io.ketill.glfw.nx;

import io.ketill.nx.NxProController;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import static io.ketill.KetillAssertions.*;
import static io.ketill.glfw.nx.GlfwNxProAdapter.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwNxProAdapterTest {

    private static final Random RANDOM = new Random();

    private static long ptr_glfwWindow;
    private static int glfwJoystick;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
        glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);
    }

    private NxProController controller;

    @BeforeEach
    void wrangleJoystick() {
        this.controller = wrangle(ptr_glfwWindow, glfwJoystick);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(controller,
                NxProController.SENSOR_ACCELEROMETER,
                NxProController.SENSOR_GYROSCOPE,
                NxProController.INTERNAL_BATTERY,
                NxProController.FEATURE_LED);
    }

    @Test
    void testUpdateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(8);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            /* update LS axes for next test */
            Vector3f ls = new Vector3f(RANDOM.nextFloat(),
                    RANDOM.nextFloat(), 0.00F);
            axes.put(MAPPING_LS.glfwXAxis, ls.x);
            axes.put(MAPPING_LS.glfwYAxis, ls.y);

            controller.poll(); /* update sticks */

            ls.y *= -1.0F;
            NxProController.CALIBRATION.applyTo(ls);
            assertEquals(ls.x, controller.ls.getX());
            assertEquals(ls.y, controller.ls.getY());

            /* update RS axes for next test */
            Vector3f rs = new Vector3f(RANDOM.nextFloat(),
                    RANDOM.nextFloat(), 0.00F);
            axes.put(MAPPING_RS.glfwXAxis, rs.x);
            axes.put(MAPPING_RS.glfwYAxis, rs.y);

            controller.poll(); /* update sticks */

            rs.y *= -1.0F;
            NxProController.CALIBRATION.applyTo(rs);
            assertEquals(rs.x, controller.rs.getX());
            assertEquals(rs.y, controller.rs.getY());
        }
    }

    @Test
    void testUpdateProTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            ByteBuffer buttons = ByteBuffer.allocate(32);
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);

            /* update button states for next test */
            buttons.put(INDEX_ZL, (byte) GLFW_PRESS);
            buttons.put(INDEX_ZR, (byte) GLFW_PRESS);

            controller.poll(); /* update triggers */
            assertEquals(1.0F, controller.lt.getForce());
            assertEquals(1.0F, controller.rt.getForce());

            /* update button states for next test */
            buttons.put(INDEX_ZL, (byte) GLFW_RELEASE);
            buttons.put(INDEX_ZR, (byte) GLFW_RELEASE);

            controller.poll(); /* update triggers */
            assertEquals(0.0F, controller.lt.getForce());
            assertEquals(0.0F, controller.rt.getForce());
        }
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}