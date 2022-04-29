package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class PlayerLedTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new PlayerLed(null));
    }

    private PlayerLed led;

    @BeforeEach
    void setup() {
        this.led = new PlayerLed("led");
    }

    @Test
    void ledCount() {
        assertEquals(4, led.ledCount);
    }

}
