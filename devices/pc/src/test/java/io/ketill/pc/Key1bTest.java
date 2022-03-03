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
    void pressed() {
        key.pressed = true;
        assertTrue(key.pressed());
        key.pressed = false;
        assertFalse(key.pressed());
    }

}
