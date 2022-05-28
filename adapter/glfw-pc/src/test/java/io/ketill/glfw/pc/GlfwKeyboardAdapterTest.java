package io.ketill.glfw.pc;

import io.ketill.MappedFeatureRegistry;
import io.ketill.pc.Keyboard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwKeyboardAdapterTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    private Keyboard keyboard;

    @BeforeEach
    void wrangleKeyboard() {
        this.keyboard = GlfwKeyboardAdapter.wrangle(ptr_glfwWindow);
    }

    @Test
    void testMapKey() {
        /* create adapter from mocks for next test */
        Keyboard keyboard = mock(Keyboard.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        GlfwKeyboardAdapter adapter = new GlfwKeyboardAdapter(keyboard,
                registry, ptr_glfwWindow);

        /*
         * It would not make sense to map a null key or for a key to be
         * mapped to a negative index. Assume these were mistakes by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapKey(null, GLFW_KEY_SPACE));
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.mapKey(Keyboard.KEY_SPACE, -1));
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(keyboard);
    }

    @Test
    void testUpdateKey() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetKey(ptr_glfwWindow, GLFW_KEY_SPACE))
                    .thenReturn(GLFW_PRESS);
            keyboard.poll(); /* update keyboard keys */
            assertTrue(keyboard.space.isPressed());

            glfw.when(() -> glfwGetKey(ptr_glfwWindow, GLFW_KEY_SPACE))
                    .thenReturn(GLFW_RELEASE);
            keyboard.poll(); /* update keyboard keys */
            assertFalse(keyboard.space.isPressed());
        }
    }

    @Test
    void testIsDeviceConnected() {
        /*
         * For simplicity, keyboards are assumed to always be connected to
         * the computer. As such, this method should always return true.
         */
        assertTrue(keyboard.isConnected());
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}