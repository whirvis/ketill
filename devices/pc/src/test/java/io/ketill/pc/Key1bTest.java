package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Key1bTest {

    private Key1b key;

    @BeforeEach
    void setup() {
        this.key = new Key1b();
    }

    @Test
    void isPressed() {
        key.pressed = true;
        assertTrue(key.isPressed());
        key.pressed = false;
        assertFalse(key.isPressed());
    }

    @Test
    void isHeld() {
        key.held = true;
        assertTrue(key.isHeld());
        key.held = false;
        assertFalse(key.isHeld());
    }

}
