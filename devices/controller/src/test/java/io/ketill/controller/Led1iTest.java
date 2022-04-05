package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Led1iTest {

    private Led1i led;

    @BeforeEach
    void setup() {
        this.led = new Led1i();
    }

    @Test
    void getValue() {
        assertEquals(1, led.getValue());
    }

    @Test
    void getMode() {
        assertEquals(Led1i.MODE_NUMBER, led.getMode());
    }

    @Test
    void setNumber() {
        led.setNumber(4);
        assertEquals(4, led.getValue());
        assertEquals(Led1i.MODE_NUMBER, led.getMode());
    }

    @Test
    void setPattern() {
        led.setPattern(0b1111);
        assertEquals(0b1111, led.getValue());
        assertEquals(Led1i.MODE_PATTERN, led.getMode());
    }

}
