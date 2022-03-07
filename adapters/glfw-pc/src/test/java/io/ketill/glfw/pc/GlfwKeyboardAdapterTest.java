package io.ketill.glfw.pc;

import io.ketill.MappedFeatureRegistry;
import io.ketill.RegisteredFeature;
import io.ketill.pc.Keyboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwKeyboardAdapterTest {

    private long ptr_glfwWindow;
    private Keyboard keyboard;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);

        this.keyboard = GlfwKeyboardAdapter.wrangle(ptr_glfwWindow);
    }

    @Test
    void mapKey() {
        /* create adapter from mocks for next test */
        Keyboard keyboard = mock(Keyboard.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        GlfwKeyboardAdapter adapter = new GlfwKeyboardAdapter(keyboard,
                registry, ptr_glfwWindow);

        /*
         * It would not make sense to map a null key or for a key
         * to be mapped to a negative index. Assume these were a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapKey(null, GLFW_KEY_SPACE));
        assertThrows(IllegalArgumentException.class,
                () -> adapter.mapKey(Keyboard.KEY_SPACE, -1));
    }

    @Test
    void ensureAllFeaturesSupported() {
        for (RegisteredFeature<?, ?> rf : keyboard.getFeatures()) {
            assertTrue(keyboard.isFeatureSupported(rf.feature));
        }
    }

    @Test
    void updateKey() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetKey(ptr_glfwWindow, GLFW_KEY_SPACE))
                    .thenReturn(GLFW_PRESS);
            keyboard.poll(); /* update keyboard keys */
            assertTrue(keyboard.space.pressed());

            glfw.when(() -> glfwGetKey(ptr_glfwWindow, GLFW_KEY_SPACE))
                    .thenReturn(GLFW_RELEASE);
            keyboard.poll(); /* update keyboard keys */
            assertFalse(keyboard.space.pressed());
        }
    }

    @Test
    void isDeviceConnected() {
        /*
         * For simplicity, keyboards are assumed to always
         * be connected to the computer. As a result, this
         * method should always return true.
         */
        assertTrue(keyboard.isConnected());
    }

    @AfterEach
    void shutdown() {
        if (ptr_glfwWindow != 0x00) {
            glfwDestroyWindow(ptr_glfwWindow);
            glfwTerminate();
        }
    }

}