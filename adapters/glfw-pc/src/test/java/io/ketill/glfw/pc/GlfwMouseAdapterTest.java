package io.ketill.glfw.pc;

import io.ketill.MappedFeatureRegistry;
import io.ketill.pc.Mouse;
import org.joml.Vector2f;
import org.junit.jupiter.api.AfterEach;
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
         * It would not make sense to map a null button or for a button
         * to be mapped to a negative index. Assume these were mistakes
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapButton(null, GLFW_MOUSE_BUTTON_1));
        assertThrows(IllegalArgumentException.class,
                () -> adapter.mapButton(Mouse.BUTTON_M1, -1));
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(mouse);
    }

    @Test
    void updateButton() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetMouseButton(ptr_glfwWindow,
                    GLFW_MOUSE_BUTTON_1)).thenReturn(GLFW_PRESS);
            mouse.poll(); /* update mouse buttons */
            assertTrue(mouse.m1.isPressed());

            glfw.when(() -> glfwGetMouseButton(ptr_glfwWindow,
                    GLFW_MOUSE_BUTTON_1)).thenReturn(GLFW_RELEASE);
            mouse.poll(); /* update mouse buttons */
            assertFalse(mouse.m1.isPressed());
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
             * When polled, the mouse adapter should set the value of the
             * currentPos vector to the current position of the mouse on
             * screen as reported by GLFW.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.getX());
            assertEquals(4.56F, mouse.cursor.getY());

            /* incorrectly set cursor position */
            Vector2f cursorPos = (Vector2f) mouse.cursor.getPosition();
            cursorPos.x = 0.00F;
            cursorPos.y = 0.00F;

            /*
             * When the position of the mouse cursor is set incorrectly by
             * the user, the device adapter should write over the data and
             * do nothing else. The user must make use of use setPosition()
             * found in CursorState to move the cursor somewhere else.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.getX());
            assertEquals(4.56F, mouse.cursor.getY());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    0.00F, 0.00F), never());

            /* correctly set cursor position */
            mouse.cursor.setPosition(7.89F, 1.01F);

            /*
             * When the position of the mouse cursor is set correctly by
             * the user, the device adapter should fulfill the request on
             * the next poll. Furthermore, it should update the value of
             * currentPos to equal the requested position.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(7.89F, mouse.cursor.getX());
            assertEquals(1.01F, mouse.cursor.getY());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    7.89F, 1.01F));

            /*
             * Until the user requests to change the mouse cursor position
             * again, the device adapter should not update its position.
             * After polling the mouse, the currentPos vector should equal
             * the value it was set to at the beginning of this test.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(1.23F, mouse.cursor.getX());
            assertEquals(4.56F, mouse.cursor.getY());
            glfw.verify(() -> glfwSetCursorPos(ptr_glfwWindow,
                    7.89, 1.01F), never());

            /*
             * When the user sets the mouse to no longer be visible, the
             * device adapter must complete that request on the next poll.
             */
            mouse.cursor.setVisible(false);
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN));

            /*
             * If the user doesn't change the state of mouse visibility, the
             * adapter must not fulfill the request again. This is done in an
             * attempt to increase performance.
             */
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN), times(1));

            /*
             * When the user sets the mouse to now be visible at this moment,
             * the device adapter must complete that request on the next poll.
             */
            mouse.cursor.setVisible(true);
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_NORMAL));

            /*
             * If the user doesn't change the state of mouse visibility, the
             * adapter must not fulfill the request again. This is done in an
             * attempt to increase performance.
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
         * For simplicity, mice are assumed to always be connected to the
         * computer. As such, this method should always return true.
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