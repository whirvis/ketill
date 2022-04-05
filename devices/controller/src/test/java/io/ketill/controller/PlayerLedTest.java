package io.ketill.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerLedTest {

    @Test
    void __init__() {
        /*
         * It would not make sense for a player LED to have
         * zero or less LED indicators. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerLed("led", 0));

        /* ensure proper default ledCount */
        PlayerLed led = new PlayerLed("led");
        assertEquals(4, led.ledCount);
    }

}
