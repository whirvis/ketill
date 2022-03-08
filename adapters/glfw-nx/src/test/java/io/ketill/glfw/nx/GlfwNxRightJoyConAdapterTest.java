package io.ketill.glfw.nx;

import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import io.ketill.nx.NxRightJoyCon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;

class GlfwNxRightJoyConAdapterTest {

    private long ptr_glfwWindow;
    private NxRightJoyCon joycon;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);

        this.joycon = GlfwNxRightJoyConAdapter.wrangle(ptr_glfwWindow,
                GLFW_JOYSTICK_1);
    }

    @Test
    void ensureAllFeaturesSupported() {
        List<IoFeature<?>> unsupported = new ArrayList<>();
        unsupported.add(NxRightJoyCon.FEATURE_LED);

        for (RegisteredFeature<?, ?> rf : joycon.getFeatures()) {
            if (!unsupported.contains(rf.feature)) {
                assertTrue(joycon.isFeatureSupported(rf.feature));
            }
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