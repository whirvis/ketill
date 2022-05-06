package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class TriggerStateTest {

    private TriggerStateZ internal;
    private TriggerState container;

    @BeforeEach
    void createState() {
        this.internal = new TriggerStateZ();
        this.container = new TriggerState(internal);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new TriggerState(null));
    }

    @Test
    void testGetForce() {
        internal.force = 1.0F;
        assertEquals(1.0f, container.getForce());
        internal.force = 0.0F;
        assertEquals(0.0F, container.getForce());
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
