package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class KeyPressTest {

    private KeyPressZ internal;
    private KeyPress container;

    @BeforeEach
    void createState() {
        this.internal = new KeyPressZ();
        this.container = new KeyPress(internal);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new KeyPress(null));
    }

    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @Test
    void testIsHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

}
