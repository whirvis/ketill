package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MotorVibrationTest {

    private MotorVibration vibration;

    @BeforeEach
    void setup() {
        this.vibration = new MotorVibration();
    }

    @Test
    void getStrength() {
        assertEquals(0.0F, vibration.getStrength());
    }

    @Test
    void setStrength() {
        assertSame(vibration, vibration.setStrength(0.0F));

        /*
         * This next float random is generated in a range of 0.0F to 1.0F.
         * As such, the setStrength() should not cap it and getString() is
         * expected to return the exact value it was set to.
         */
        float strength = new Random().nextFloat();
        vibration.setStrength(strength);
        assertEquals(strength, vibration.getStrength());

        /*
         * To prevent unexpected behavior, the motor vibration caps the
         * vibration strength between a value of 0.0F and 1.0F. This is
         * done for the convenience of I/O device adapters.
         */
        vibration.setStrength(-1.0F);
        assertEquals(0.0F, vibration.getStrength());
        vibration.setStrength(2.0F);
        assertEquals(1.0F, vibration.getStrength());
    }

}
