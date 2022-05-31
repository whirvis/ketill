package io.ketill.awt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AwtKeyboardListenerTest {

    private Component component;
    private AwtKeyboardListener listener;
    private AwtKeyMapping mapping;

    @BeforeEach
    void createListener() {
        this.component = mock(Component.class);
        this.listener = new AwtKeyboardListener(component);
        this.mapping = new AwtKeyMapping(KeyEvent.VK_SPACE,
                KeyEvent.KEY_LOCATION_STANDARD);
    }

    @Test
    void testIsPressed() {
        assertThrows(NullPointerException.class,
                () -> listener.isPressed(null));
        assertFalse(listener.isPressed(mapping));
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
         * When the AWT keyboard listener is initialized, it must add itself
         * as a key listener to the AWT component it was given. Failing to do
         * so will result it in receiving no keyboard input events.
         */
        listener.init();
        verify(component).addKeyListener(listener);

        /*
         * After the AWT keyboard listener has first been initialized, it
         * would not make sense to initialize it again. Assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(IllegalStateException.class, () -> listener.init());
    }

    @Test
    void testKeyTyped() {
        /* this event is ignored, so nothing should be thrown */
        assertDoesNotThrow(() -> listener.keyTyped(null));
    }

    @Test
    void testKeyPressedAndKeyReleased() {
        KeyEvent event = mock(KeyEvent.class);
        when(event.getKeyCode()).thenReturn(mapping.keyCode);
        when(event.getKeyLocation()).thenReturn(mapping.keyLocation);

        listener.keyPressed(event);
        assertTrue(listener.isPressed(mapping));

        listener.keyReleased(event);
        assertFalse(listener.isPressed(mapping));
    }

}
