package io.ketill.controller;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
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
    void testGetUpperBound() {
        assertEquals(0.77F, calibration.getUpperBound());
    }

    @Test
    void testGetLowerBound() {
        assertEquals(0.00F, calibration.getLowerBound());
    }

    @Test
    void testApply() {
        float value = 0.1925F; /* pressed by one quarter */
        assertEquals(0.25F, calibration.apply(value));
    }

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(AnalogTriggerCalibration.class).verify();
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(AnalogTriggerCalibration.class, calibration);
    }

}
