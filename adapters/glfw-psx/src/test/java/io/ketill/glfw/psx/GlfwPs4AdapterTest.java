package io.ketill.glfw.psx;

import io.ketill.psx.Ps4Controller;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.FloatBuffer;
import java.util.Random;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwPs4AdapterTest {

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

    private Ps4Controller controller;

    @BeforeEach
    void wrangleJoystick() {
        this.controller = GlfwPs4Adapter.wrangle(ptr_glfwWindow, glfwJoystick);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(controller, Ps4Controller.MOTOR_STRONG,
                Ps4Controller.MOTOR_WEAK, Ps4Controller.FEATURE_LIGHTBAR);
    }

    @Test
    void testUpdateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick)).thenReturn(axes);

            /* generate axis values for next test */
            float lsYValue = RANDOM.nextFloat();
            float rsYValue = RANDOM.nextFloat();
            axes.put(GlfwPs4Adapter.MAPPING_LS.glfwYAxis, lsYValue);
            axes.put(GlfwPs4Adapter.MAPPING_RS.glfwYAxis, rsYValue);

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
            axes.put(GlfwPs4Adapter.AXIS_LT, ltValue);
            axes.put(GlfwPs4Adapter.AXIS_RT, rtValue);

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
