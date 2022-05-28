package io.ketill.awt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AwtMouseListenerTest {

    private Component component;
    private AwtMouseListener listener;

    @BeforeEach
    void createListener() {
        this.component = mock(Component.class);
        this.listener = new AwtMouseListener(component);
    }

    @Test
    void testIsPressed() {
        int buttonCount = AwtMouseListener.MOUSE_BUTTON_COUNT;

        assertThrows(IndexOutOfBoundsException.class,
                () -> listener.isPressed(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> listener.isPressed(buttonCount));

        for (int i = 0; i < buttonCount; i++) {
            assertFalse(listener.isPressed(i));
        }
    }

    @Test
    void testIsInitialized() {
        assertFalse(listener.isInitialized());
        listener.init();
        assertTrue(listener.isInitialized());
    }

    @Test
    void testInit() {
        /*
         * When the AWT mouse listener is initialized, it must add itself as
         * a mouse listener to the AWT component it was given. Failing to do
         * so will result it in receiving no mouse input events.
         */
        listener.init();
        verify(component).addMouseListener(listener);

        /*
         * After the AWT mouse listener has first been initialized, it would
         * not make sense to initialize it again. Assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(IllegalStateException.class, () -> listener.init());
    }

    @Test
    void testMouseClicked() {
        /* this event is ignored, so nothing should be thrown */
        assertDoesNotThrow(() -> listener.mouseClicked(null));
    }

    @Test
    void testMousePressedAndMouseReleased() {
        int button = MouseEvent.BUTTON3;
        MouseEvent event = mock(MouseEvent.class);
        when(event.getButton()).thenReturn(button);

        listener.mousePressed(event);
        assertTrue(listener.isPressed(button));

        listener.mouseReleased(event);
        assertFalse(listener.isPressed(button));
    }

    @Test
    void testMouseEntered() {
        /* this event is ignored, so nothing should be thrown */
        assertDoesNotThrow(() -> listener.mouseEntered(null));
    }

    @Test
    void testMouseExited() {
        /* this event is ignored, so nothing should be thrown */
        assertDoesNotThrow(() -> listener.mouseExited(null));
    }

}
