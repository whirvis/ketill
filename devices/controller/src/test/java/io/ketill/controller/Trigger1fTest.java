package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Trigger1fTest {

    private Trigger1f trigger;

    @BeforeEach
    void setup() {
        this.trigger = new Trigger1f();
    }

    @Test
    void getForce() {
        trigger.force = 1.0F;
        assertEquals(1.0F, trigger.getForce());
        trigger.force = 0.0F;
        assertEquals(0.0F, trigger.getForce());
    }

    @Test
    void button() {
        assertNotNull(trigger.button());
    }

    @Test
    void isPressed() {
        trigger.button.pressed = true;
        assertTrue(trigger.isPressed());
        trigger.button.pressed = false;
        assertFalse(trigger.isPressed());
    }

    @Test
    void isHeld() {
        trigger.button.held = true;
        assertTrue(trigger.isHeld());
        trigger.button.held = false;
        assertFalse(trigger.isHeld());
    }

}
