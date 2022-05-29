package io.ketill.glfw.pc;

import io.ketill.MappedFeatureRegistry;
import io.ketill.pc.Mouse;
import org.joml.Vector2f;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.mockito.MockedStatic;

import java.awt.*;
import java.awt.image.BufferedImage;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwMouseAdapterTest {

    private static long ptr_glfwWindow;

    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
    }

    private Mouse mouse;

    @BeforeEach
    void wrangleMouse() {
        this.mouse = GlfwMouseAdapter.wrangle(ptr_glfwWindow);
    }

    @Test
    void testCreateGlfwImage() {
        /*
         * It would not make sense to create a GLFW image from a null Java
         * AWT image. As such, assume this was a mistake by the user and
         * throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> GlfwMouseAdapter.createGlfwImage(null));

        BufferedImage buffered = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_ARGB);

        /*
         * The created GLFW image should have the same dimensions as the
         * image passed to it. The pixels cannot also be verified here as
         * they are not directly accessible from the struct.
         */
        GLFWImage glfwImg = GlfwMouseAdapter.createGlfwImage(buffered);
        assertEquals(buffered.getWidth(), glfwImg.width());
        assertEquals(buffered.getHeight(), glfwImg.height());

        Image unbuffered = mock(Image.class);
        when(unbuffered.getWidth(null)).thenReturn(16);
        when(unbuffered.getHeight(null)).thenReturn(16);

        /*
         * The test below exists primarily for coverage purposes. When an
         * image does not extend from BufferedImage is passed, this method
         * should create a BufferedImage to contain it. This is achieved
         * with Java's Graphics2D API.
         *
         * We can verify that rendering calls were made with this image by
         * checking that getWidth() and getHeight() were called at least
         * two times. They are both called once by the method for creating
         * the pixel buffer later on. So, assuming they were both called
         * at least two times, that means a renderer was used.
         */
        GlfwMouseAdapter.createGlfwImage(unbuffered);
        verify(unbuffered, atLeast(2)).getWidth(null);
        verify(unbuffered, atLeast(2)).getHeight(null);
    }

    @Test
    void testMapButton() {
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
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.mapButton(Mouse.BUTTON_M1, -1));
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(mouse);
    }

    @Test
    void testUpdateButton() {
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
    void testUpdateCursor() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetCursorPos(eq(ptr_glfwWindow),
                    (double[]) any(), any())).thenAnswer(a -> {
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
                    7.89F, 1.01F), times(1));

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
                    GLFW_CURSOR_HIDDEN), times(1));

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
                    GLFW_CURSOR_NORMAL), times(1));

            /*
             * If the user doesn't change the state of mouse visibility, the
             * adapter must not fulfill the request again. This is done in an
             * attempt to increase performance.
             */
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR,
                    GLFW_CURSOR_HIDDEN), times(1));

            /*
             * The code below mocks glfwCreateCursor(), which is used by the
             * adapter when creating a custom cursor. If not mocked, Mockito
             * will return a value of zero (which in GLFW, indicates that an
             * error has occurred while trying to create a cursor).
             */
            long ptr_glfwCursor = 0x012346789ABCDEFL;
            glfw.when(() -> glfwCreateCursor(any(), anyInt(), anyInt()))
                    .thenReturn(ptr_glfwCursor);

            /*
             * When the user sets a new cursor icon that is not null, the
             * adapter should make a call to glfwCreateCursor() (which is
             * mocked above) and then set the cursor to newly created one.
             *
             * Furthermore, since this is the first non-null cursor, the
             * adapter should have made a call to glfwDestroyCursor(), as
             * there is nothing to destroy.
             */
            mouse.cursor.setIcon(new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB));
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwDestroyCursor(anyLong()), never());
            glfw.verify(() -> glfwSetCursor(ptr_glfwWindow, ptr_glfwCursor),
                    times(1));

            /*
             * When a new cursor icon is set, the adapter must free the
             * memory associated with the previous and destroy the cursor.
             * Failing to do so will result in a memory leak, which could
             * be catastrophic if the user makes use of many cursors.
             *
             * Furthermore, since the user has set the icon, the adapter
             * should use the default cursor. In GLFW, the default cursor
             * is represented by a NULL pointer (or zero in Java).
             */
            mouse.cursor.setIcon(null);
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwDestroyCursor(ptr_glfwCursor), times(1));
            glfw.verify(() -> glfwSetCursor(ptr_glfwWindow, 0L), times(1));
        }
    }

    @Test
    void testPollDevice() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mouse.poll(); /* update mouse cursor */
            glfw.verify(() -> glfwGetCursorPos(eq(ptr_glfwWindow),
                    (double[]) any(), any()));
        }
    }

    @Test
    void testIsDeviceConnected() {
        /*
         * For simplicity, mice are assumed to always be connected to the
         * computer. As such, this method should always return true.
         */
        assertTrue(mouse.isConnected());
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}