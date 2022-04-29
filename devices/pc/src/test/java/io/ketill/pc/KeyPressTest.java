package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class KeyPressTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new KeyPress(null));
    }

    private KeyPressZ internal;
    private KeyPress container;

    @BeforeEach
    void setup() {
        this.internal = new KeyPressZ();
        this.container = new KeyPress(internal);
    }

    @Test
    void isPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @Test
    void isHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

}
