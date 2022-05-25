package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalogTriggerCalibrationTest {

    private AnalogTriggerCalibration calibration;

    @BeforeEach
    void createCalibration() {
        this.calibration = new AnalogTriggerCalibration(0.77F, 0.00F);
    }

    @Test
    void testInit() {
        /*
         * Calibration would not work properly if the upper bound is not
         * greater than the lower bound. As such, assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new AnalogTriggerCalibration(0.0F, 0.0F));
    }

    @Test
    void testApply() {
        float value = 0.1925F; /* pressed by one quarter */
        assertEquals(0.25F, calibration.apply(value));
    }

}
