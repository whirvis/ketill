package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.pc.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyPressTest {

    private IoDeviceObserver observer;
    private KeyPressZ internal;
    private KeyPress container;

    @BeforeEach
    void createState() {
        Keyboard keyboard = mock(Keyboard.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(keyboard);

        KeyboardKey key = new KeyboardKey("key");
        this.internal = new KeyPressZ(key, observer);
        this.container = new KeyPress(internal);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

    @Test
    void testUpdate() {
        internal.pressed = true; /* press key */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, KeyboardKeyPressEvent.class);

        internal.pressed = false; /* release key */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, KeyboardKeyReleaseEvent.class);
    }

}
