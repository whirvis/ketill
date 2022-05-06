package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class MouseClickTest {

    private MouseClickZ internal;
    private MouseClick container;

    @BeforeEach
    void createState() {
        this.internal = new MouseClickZ();
        this.container = new MouseClick(internal);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new MouseClick(null));
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
