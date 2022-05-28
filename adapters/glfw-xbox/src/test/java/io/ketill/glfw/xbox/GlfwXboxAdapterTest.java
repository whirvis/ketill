package io.ketill.glfw.xbox;

import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.FloatBuffer;
import java.util.Random;

import static io.ketill.KetillAssertions.*;
import static io.ketill.glfw.xbox.GlfwXboxAdapter.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwXboxAdapterTest {

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

    private XboxController controller;

    @BeforeEach
    void wrangleJoystick() {
        this.controller = wrangle(ptr_glfwWindow, glfwJoystick);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(controller, XboxController.MOTOR_COARSE,
                XboxController.MOTOR_FINE);
    }

    @Test
    void testUpdateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick)).thenReturn(axes);

            /* generate axis values for next test */
            float lsYValue = RANDOM.nextFloat();
            float rsYValue = RANDOM.nextFloat();
            axes.put(MAPPING_LS.glfwYAxis, lsYValue);
            axes.put(MAPPING_RS.glfwYAxis, rsYValue);

            controller.poll(); /* update sticks */

            lsYValue *= -1.0F;
            rsYValue *= -1.0F;
            assertEquals(lsYValue, controller.ls.getPos().y());
            assertEquals(rsYValue, controller.rs.getPos().y());
        }
    }

    @Test
    void testUpdateTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick)).thenReturn(axes);

            /* generate axis values for next test */
            float ltValue = RANDOM.nextFloat();
            float rtValue = RANDOM.nextFloat();
            axes.put(AXIS_LT, ltValue);
            axes.put(AXIS_RT, rtValue);

            controller.poll(); /* update triggers */

            ltValue = (ltValue + 1.0F) / 2.0F;
            rtValue = (rtValue + 1.0F) / 2.0F;
            assertEquals(ltValue, controller.lt.getForce());
            assertEquals(rtValue, controller.rt.getForce());
        }
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}
