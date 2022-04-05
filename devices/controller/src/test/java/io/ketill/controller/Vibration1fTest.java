package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vibration1fTest {

    private Vibration1f vibration;

    @BeforeEach
    void setup() {
        this.vibration = new Vibration1f();
    }

    @Test
    void setStrength() {
        /*
         * The vibration state should clamp the provided
         * strength within 0.0F to 1.0F. This is to prevent
         * possible unexpected behavior in adapters.
         */
        vibration.setStrength(2.0F);
        assertEquals(1.0F, vibration.getStrength());
        vibration.setStrength(-1.0F);
        assertEquals(0.0F, vibration.getStrength());
    }

}
