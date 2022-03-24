package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Button1bTest {

    private Button1b button;

    @BeforeEach
    void setup() {
        this.button = new Button1b();
    }

    @Test
    void pressed() {
        button.pressed = true;
        assertTrue(button.isPressed());
        button.pressed = false;
        assertFalse(button.isPressed());
    }

}
