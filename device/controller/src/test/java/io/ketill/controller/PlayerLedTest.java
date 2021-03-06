package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class PlayerLedTest {

    private PlayerLed led;

    @BeforeEach
    void createLed() {
        this.led = new PlayerLed("led");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new PlayerLed(null));
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerLed("led", -1));
    }

    @Test
    void testGetDeviceType() {
        assertSame(Controller.class, led.getDeviceType());
    }

    @Test
    void testGetLedCount() {
        assertEquals(4, led.getLedCount());
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(PlayerLed.class, led);
    }

}
