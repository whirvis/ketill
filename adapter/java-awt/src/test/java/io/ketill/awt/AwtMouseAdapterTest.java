package io.ketill.awt;

import io.ketill.MappedFeatureRegistry;
import io.ketill.pc.Mouse;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AwtMouseAdapterTest {

    private Component component;
    private MouseListener listener;
    private Robot robot;
    private Mouse mouse;

    @BeforeEach
    void captureMouse() {
        this.component = mock(Component.class);
        when(component.isVisible()).thenReturn(true);

        /*
         * Component.getLocation() and Component.getLocationOnScreen()
         * are both used by the AWT mouse adapter for different purposes.
         * To keep things simple, make the component position (0, 0).
         */
        Point point = new Point(0, 0);
        doReturn(point).when(component).getLocation();
        doReturn(point).when(component).getLocationOnScreen();

        /*
         * The AWT mouse listener will call addMouseListener() on the
         * component when it is initialized. The mock below captures it,
         * allowing it to be directly interacted with in later tests.
         */
        doAnswer(a -> {
            this.listener = a.getArgument(0);
            return null;
        }).when(component).addMouseListener(any());

        /*
         * The mock below mocks the creation of an AWT robot, allowing us
         * to ensure that the AWT mouse adapter interacts with it properly
         * in later tests.
         */
        try (MockedConstruction<Robot> mockRobots =
                     mockConstruction(Robot.class)) {
            this.mouse = AwtMouseAdapter.capture(component);
            this.robot = mockRobots.constructed().get(0);
            mouse.poll(); /* initialize listener */
        }
    }

    @Test
    void testCapture() {
        assertThrows(NullPointerException.class,
                () -> AwtMouseAdapter.capture(null));
        assertNotNull(AwtMouseAdapter.capture(component));
    }

    @Test
    void testCaptureBackground() {
        assertThrows(NullPointerException.class,
                () -> AwtMouseAdapter.captureBackground(null));

        AwtPollWorker<Mouse> mouseWorker =
                AwtMouseAdapter.captureBackground(component);
        assertNotNull(mouseWorker);

        mouseWorker.close(); /* prevent lingering thread */
    }

    @Test
    void testMapButton() {
        /* create adapter from mocks for next test */
        Mouse mouse = mock(Mouse.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        AwtMouseAdapter adapter = new AwtMouseAdapter(mouse, registry,
                component);

        /*
         * It would not make sense to map a null button. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapButton(null, MouseEvent.BUTTON1));
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        assertAllFeaturesSupported(mouse);
    }

    @Test
    void testUpdateButton() {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(MouseEvent.BUTTON1);

        listener.mousePressed(event);
        mouse.poll(); /* update mouse buttons */
        assertTrue(mouse.m1.isPressed());

        listener.mouseReleased(event);
        mouse.poll(); /* update mouse buttons */
        assertFalse(mouse.m1.isPressed());
    }

    @Test
    void testUpdateCursorPos() {
        try (MockedStatic<MouseInfo> mouseInfo = mockStatic(MouseInfo.class)) {
            Point cursorLoc = new Point();
            PointerInfo pointerInfo = mock(PointerInfo.class);
            when(pointerInfo.getLocation()).thenReturn(cursorLoc);
            mouseInfo.when(MouseInfo::getPointerInfo).thenReturn(pointerInfo);

            /*
             * The mouse cursor is polled at the beginning of this test to
             * initialize the mouse listener. This causes the position to be
             * a non-zero value, which we don't want yet. The code below has
             * the cursor position to be reset to zero, and then prepares it
             * to be set to (123, 456) on the next valid call to poll().
             */
            cursorLoc.setLocation(0, 0);
            mouse.poll(); /* update cursor position */
            cursorLoc.setLocation(123, 456);

            try (MockedStatic<GraphicsEnvironment> graphics =
                         mockStatic(GraphicsEnvironment.class)) {
                /* mock headless graphics environment */
                graphics.when(GraphicsEnvironment::isHeadless).thenReturn(true);

                /*
                 * In a headless environment, the cursor position should not
                 * be updated. No errors should be thrown as it is assumed
                 * the user may eventually plug a display, keyboard, and/or
                 * mouse into the computer.
                 */
                mouse.poll(); /* update cursor position */
                assertEquals(0, mouse.cursor.getX());
                assertEquals(0, mouse.cursor.getY());

                /*
                 * If a mouse is created without being able to instantiate
                 * a robot, it should indicate it does not have the ability
                 * to move the cursor to a different position.
                 */
                Mouse mouseWithoutRobot = AwtMouseAdapter.capture(component);
                assertFalse(mouseWithoutRobot.cursor.canSetPosition());
            }

            /* further testing requires non-headless environment */
            assumeFalse(GraphicsEnvironment.isHeadless());

            /*
             * When the component is not currently visible, it becomes
             * impossible to retrieve the current position of the mouse
             * cursor. As such, the method should return and not update
             * the cursor position.
             */
            when(component.isVisible()).thenReturn(false);
            mouse.poll(); /* update cursor position */
            assertEquals(0, mouse.cursor.getX());
            assertEquals(0, mouse.cursor.getY());
            when(component.isVisible()).thenReturn(true);

            /* cache previous location for next test */
            Point locationOnScreen = component.getLocationOnScreen();

            /*
             * Sometimes, the component will not update isVisible() in time
             * for this method to return. The method should catch this, and
             * return from the method when this occurs.
             */
            doThrow(IllegalComponentStateException.class)
                    .when(component).getLocationOnScreen();
            mouse.poll(); /* update cursor position */
            assertEquals(0, mouse.cursor.getX());
            assertEquals(0, mouse.cursor.getY());
            doReturn(locationOnScreen)
                    .when(component).getLocationOnScreen();

            /*
             * When polled, the mouse adapter should set the value of the
             * currentPos vector to the current position of the mouse on
             * screen as reported by the AWT component.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(123, mouse.cursor.getX());
            assertEquals(456, mouse.cursor.getY());

            /* incorrectly set cursor position */
            Vector2f cursorPos = (Vector2f) mouse.cursor.getPosition();
            cursorPos.x = 0;
            cursorPos.y = 0;

            /*
             * When the position of the mouse cursor is set incorrectly by
             * the user, the device adapter should write over the data and
             * do nothing else. The user must make use of use setPosition()
             * found in CursorState to move the cursor somewhere else.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(123, mouse.cursor.getX());
            assertEquals(456, mouse.cursor.getY());
            verify(robot, never()).mouseMove(0, 0);

            /* correctly set cursor position */
            mouse.cursor.setPosition(789, 101);

            /*
             * When the position of the mouse cursor is set correctly by
             * the user, the device adapter should fulfill the request on
             * the next poll. Furthermore, it should update the value of
             * currentPos to equal the requested position.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(789, mouse.cursor.getX());
            assertEquals(101, mouse.cursor.getY());
            verify(robot, times(1)).mouseMove(789, 101);

            /*
             * Until the user requests to change the mouse cursor position
             * again, the device adapter should not update its position.
             * After polling the mouse, the currentPos vector should equal
             * the value it was set to at the beginning of this test.
             */
            mouse.poll(); /* update mouse cursor */
            assertEquals(123, mouse.cursor.getX());
            assertEquals(456, mouse.cursor.getY());
            verify(robot, times(1)).mouseMove(789, 101);
        }
    }

    @Test
    void testUpdateCursorVisibility() {
        try (MockedStatic<GraphicsEnvironment> graphics =
                     mockStatic(GraphicsEnvironment.class)) {
            /* mock headless graphics environment */
            graphics.when(GraphicsEnvironment::isHeadless).thenReturn(true);

            /*
             * In a headless environment, the cursor visibility should not
             * be updated even if the user changes it. No errors should be
             * thrown as it is assumed the user may later plug a display,
             * keyboard, and/or mouse into the computer.
             */
            mouse.cursor.setVisible(false);
            mouse.poll(); /* update mouse cursor */
            verify(component, never()).setCursor(any());
        }

        /* further testing requires non-headless environment */
        assumeFalse(GraphicsEnvironment.isHeadless());

        /*
         * When the user sets the mouse to no longer be visible, the
         * device adapter must complete that request on the next poll.
         */
        mouse.cursor.setVisible(false);
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(notNull());

        /*
         * If the user doesn't change the state of mouse visibility, the
         * adapter must not fulfill the request again. This is done in an
         * attempt to increase performance.
         */
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(notNull());

        /*
         * When the user sets the mouse to now be visible at this moment,
         * the device adapter must complete that request on the next poll.
         */
        mouse.cursor.setVisible(true);
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(null);

        /*
         * If the user doesn't change the state of mouse visibility, the
         * adapter must not fulfill the request again. This is done in an
         * attempt to increase performance.
         */
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(null);
    }


    @Test
    void testUpdateCursorIcon() {
        try (MockedStatic<GraphicsEnvironment> graphics =
                     mockStatic(GraphicsEnvironment.class)) {
            /* mock headless graphics environment */
            graphics.when(GraphicsEnvironment::isHeadless).thenReturn(true);

            /*
             * In a headless environment, the cursor icon should not be
             * updated even if the user changes it. No errors should be
             * thrown as it is assumed the user may later plug a display,
             * keyboard, and/or mouse into the computer.
             */
            mouse.cursor.setIcon(null);
            mouse.poll(); /* update mouse cursor */
            verify(component, never()).setCursor(any());
        }

        /* further testing requires non-headless environment */
        assumeFalse(GraphicsEnvironment.isHeadless());

        /*
         * When the mouse cursor icon is set to null, the adapter should
         * simply set the cursor to default. For Java AWT, the default
         * cursor is simply null.
         */
        mouse.cursor.setIcon(null);
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(null);

        /*
         * When the mouse cursor icon is set to a non-null value, the
         * adapter should set the cursor to a non-null value (presumably
         * a cursor which uses the provided image as its icon).
         */
        BufferedImage img = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_ARGB);
        mouse.cursor.setIcon(img);
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(notNull());

        /*
         * If the mouse cursor is not visible when its icon is set to
         * any value, the adapter should not make a call to update the
         * current cursor. This is because doing so would erroneously
         * make the cursor visible again. The custom cursor will be
         * visible when the user sets the cursor to be visible again.
         */
        mouse.cursor.setVisible(false);
        mouse.cursor.setIcon(null);
        mouse.poll(); /* update mouse cursor */
        verify(component, times(1)).setCursor(null);
    }

    @Test
    void testPollDevice() {
        /*
         * The AWT mouse listener was initialized by createMouse() during
         * setup. As a result, there's nothing to verify here. Just make
         * sure that calling poll() does not cause an exception.
         */
        assertDoesNotThrow(() -> mouse.poll());
    }

    @Test
    void testIsDeviceConnected() {
        /*
         * For simplicity, mice are assumed to always be connected to the
         * computer. As such, this method should always return true.
         */
        assertTrue(mouse.isConnected());
    }

}
