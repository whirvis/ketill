package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class ButtonStateTest {

    private ButtonStateZ internal;
    private ButtonState container;

    @BeforeEach
    void createState() {
        this.internal = new ButtonStateZ();
        this.container = new ButtonState(internal);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new ButtonState(null));
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
