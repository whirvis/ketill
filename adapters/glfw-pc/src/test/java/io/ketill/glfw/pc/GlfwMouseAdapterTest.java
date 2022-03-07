package io.ketill.glfw.pc;

import io.ketill.MappedFeatureRegistry;
import io.ketill.RegisteredFeature;
import io.ketill.pc.Mouse;
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
class GlfwMouseAdapterTest {

    private long ptr_glfwWindow;
    private Mouse mouse;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);

        this.mouse = GlfwMouseAdapter.wrangle(ptr_glfwWindow);
    }

    @Test
    void mapButton() {
        /* create adapter from mocks for next test */
        Mouse mouse = mock(Mouse.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        GlfwMouseAdapter adapter = new GlfwMouseAdapter(mouse,
                registry, ptr_glfwWindow);

        /*
         * It would not make sense to map a null key or for a key
         * to be mapped to a negative index. Assume these were a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapButton(null, GLFW_MOUSE_BUTTON_1));
        assertThrows(IllegalArgumentException.class,
                () -> adapter.mapButton(Mouse.BUTTON_M1, -1));
    }

    @Test
    void ensureAllFeaturesSupported() {
        for (RegisteredFeature<?, ?> rf : mouse.getFeatures()) {
            assertTrue(mouse.isFeatureSupported(rf.feature));
        }
    }

    @Test
    void updateButton() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetMouseButton(ptr_glfwWindow,
                    GLFW_MOUSE_BUTTON_1)).thenReturn(GLFW_PRESS);
            mouse.poll(); /* update mouse buttons */
            assertTrue(mouse.m1.clicked());

            glfw.when(() -> glfwGetMouseButton(ptr_glfwWindow,
                    GLFW_MOUSE_BUTTON_1)).thenReturn(GLFW_RELEASE);
            mouse.poll(); /* update mouse buttons */
            assertFalse(mouse.m1.clicked());
        }
    }

    @Test
    void updateCursor() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetCursorPos(eq(ptr_glfwWindow),
                    (double[]) any(), any())).thenAnswer((a) -> {
                a.getArgument(1, double[].class)[0] = 1.23F;
                a.getArgument(2, double[].class)[0] = 4.56F;
                return null;
            });

            /*
             * When polled, the mouse adapter should set the value
             * of the currentPos vector to the current position of
             * the mouse on screen as reported by GLFW.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.currentPos.x());
            assertEquals(4.56F, mouse.cursor.currentPos.y());

            /* incorrectly set cursor position */
            mouse.cursor.currentPos.x = 0.00F;
            mouse.cursor.currentPos.y = 0.00F;

            /*
             * When the mouse cursor position is set incorrectly
             * by the user, the device adapter should just write
             * over the incorrectly set position. The user must
             * make use of setPosition() in CursorState in order
             * to move the mouse cursor somewhere else.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.currentPos.x());
            assertEquals(4.56F, mouse.cursor.currentPos.y());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    0.00F, 0.00F), never());

            /* correctly set cursor position */
            mouse.cursor.setPosition(7.89F, 1.01F);

            /*
             * When the position of the mouse cursor is properly
             * set, the device adapter should move the cursor to
             * the requested position and update the currentPos
             * to equal the requested position.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(7.89F, mouse.cursor.currentPos.x());
            assertEquals(1.01F, mouse.cursor.currentPos.y());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    7.89F, 1.01F));

            /*
             * Until the user requests to change the mouse cursor
             * position again, the device adapter should not move
             * the mouse cursor. As such, after polling the mouse,
             * the currentPos vector should be reset to the values
             * it was set to at the beginning of this test.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.currentPos.x());
            assertEquals(4.56F, mouse.cursor.currentPos.y());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    7.89, 1.01F), never());

            /*
             * When the user sets the mouse to no longer be
             * visible, the device adapter must complete that
             * request on the next poll.
             */
            mouse.cursor.setVisible(false);
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN));

            /*
             * If the user doesn't change the state of mouse
             * visibility, the adapter must not fulfill the
             * request again. This is done in an attempt to
             * increase performance.
             */
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN), times(1));

            /*
             * When the user sets the mouse to now be visible
             * at this moment, the device adapter must complete
             * that request on the next poll.
             */
            mouse.cursor.setVisible(true);
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_NORMAL));

            /*
             * If the user doesn't change the state of mouse
             * visibility, the adapter must not fulfill the
             * request again. This is done in an attempt to
             * increase performance.
             */
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN), times(1));
        }
    }

    @Test
    void pollDevice() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwGetCursorPos(eq(ptr_glfwWindow),
                    (double[]) any(), any()));
        }
    }

    @Test
    void isDeviceConnected() {
        /*
         * For simplicity, mice are assumed to always be
         * connected to the computer. As a result, this
         * method should always return true.
         */
        assertTrue(mouse.isConnected());
    }

    @AfterEach
    void shutdown() {
        if (ptr_glfwWindow != 0x00) {
            glfwDestroyWindow(ptr_glfwWindow);
            glfwTerminate();
        }
    }

}