package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class TriggerStateTest {

    private TriggerStateZ internal;
    private TriggerState container;

    @BeforeEach
    void setup() {
        this.internal = new TriggerStateZ();
        this.container = new TriggerState(internal);
    }

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new TriggerState(null));
    }

    @Test
    void getForce() {
        internal.force = 1.0F;
        assertEquals(1.0f, container.getForce());
        internal.force = 0.0F;
        assertEquals(0.0F, container.getForce());
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
