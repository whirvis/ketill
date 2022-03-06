package io.ketill.glfw.xbox;

import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwXboxAdapterTest {

    private static final Random RANDOM = new Random();

    private long ptr_glfwWindow;
    private int glfwJoystick;
    private XboxController controller;

    @BeforeEach
    void setup() {
        /*
         * For the test to successfully execute, GLFW must
         * successfully initialize. If it fails to do so,
         * that is fine. It just means the current machine
         * does not have access to GLFW.
         */
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);
        this.glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);

        this.controller = GlfwXboxAdapter.wrangle(ptr_glfwWindow, glfwJoystick);
    }

    @Test
    void ensureAllFeaturesSupported() {
        /*
         * All features in the list are ignored, as they are
         * intentionally not supported. This test is to ensure
         * all originally intended features are supported.
         */
        List<IoFeature<?>> unsupported = new ArrayList<>();
        unsupported.add(XboxController.MOTOR_COARSE);
        unsupported.add(XboxController.MOTOR_FINE);

        for (RegisteredFeature<?, ?> rf : controller.getFeatures()) {
            if (!unsupported.contains(rf.feature)) {
                assertTrue(controller.isFeatureSupported(rf.feature));
            }
        }
    }

    @Test
    void updateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer xboxAxes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick)).thenReturn(xboxAxes);

            /* generate axis values for next test */
            float lsYValue = RANDOM.nextFloat();
            float rsYValue = RANDOM.nextFloat();
            xboxAxes.put(GlfwXboxAdapter.LS_MAPPING.glfwYAxis, lsYValue);
            xboxAxes.put(GlfwXboxAdapter.RS_MAPPING.glfwYAxis, rsYValue);

            controller.poll(); /* update sticks */

            /*
             * The analog sticks on an XBOX controller, for some
             * reason, have their Y-axes inverted. This ensures
             * that they are correctly flipped around.
             */
            lsYValue *= -1.0F;
            rsYValue *= -1.0F;
            assertEquals(lsYValue, controller.ls.y());
            assertEquals(rsYValue, controller.rs.y());
        }
    }

    @Test
    void updateTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer xboxAxes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick)).thenReturn(xboxAxes);

            /* generate axis values for next test */
            float ltValue = RANDOM.nextFloat();
            float rtValue = RANDOM.nextFloat();
            xboxAxes.put(GlfwXboxAdapter.AXIS_LT, ltValue);
            xboxAxes.put(GlfwXboxAdapter.AXIS_RT, rtValue);

            controller.poll(); /* update triggers */

            /*
             * Ensure that analog trigger values are converted
             * from a scale of -1.0F to 1.0F to a proper scale
             * of 0.0F to 1.0F.
             */
            ltValue = (ltValue + 1.0F) / 2.0F;
            rtValue = (rtValue + 1.0F) / 2.0F;
            assertEquals(ltValue, controller.lt.force());
            assertEquals(rtValue, controller.rt.force());
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
