package io.ketill.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalogTriggerTest {

    @Test
    void isPressed() {
        assertFalse(AnalogTrigger.isPressed(0.0F));
        assertTrue(AnalogTrigger.isPressed(1.0F));
    }

}
