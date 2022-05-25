package io.ketill.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class AnalogStickCalibrationTest {

    private AnalogStickCalibration calibration;

    @BeforeEach
    void createCalibration() {
        Vector2f upperBound = new Vector2f(0.77F, 0.77F);
        Vector2f lowerBound = new Vector2f(-0.77F, -0.77F);
        this.calibration = new AnalogStickCalibration(upperBound, lowerBound);
    }

    @Test
    void testInit() {
        /*
         * It would not make sense to create a calibration with a null upper
         * bound or null lower bound. As such, assume these were mistakes by
         * the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new AnalogStickCalibration(null, new Vector2f()));
        assertThrows(NullPointerException.class,
                () -> new AnalogStickCalibration(new Vector2f(), null));

        /*
         * Calibration would not work properly if the upper bound is not
         * greater than the lower bound. As such, assume this was a mistake
         * by the user and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new AnalogStickCalibration(0.0F, 0.0F, 0.0F, 0.0F));
    }

    @Test
    void testApplyTo() {
        /*
         * It would not make sense to apply a calibration to a null vector.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> calibration.applyTo((Vector2f) null));
        assertThrows(NullPointerException.class,
                () -> calibration.applyTo((Vector3f) null));

        /* generate random 2D vector for next test */
        Vector2f raw2f = new Vector2f();
        raw2f.x = 0.385F; /* halfway to the right */
        raw2f.y = -0.77F; /* fully facing downwards */

        Vector2f calibrated2f = new Vector2f(raw2f);
        calibration.applyTo(calibrated2f);
        assertEquals(0.5F, calibrated2f.x);
        assertEquals(-1.0F, calibrated2f.y);

        /* generate random 3D vector next test */
        Vector3f raw3f = new Vector3f();
        raw3f.x = raw2f.x; /* copy X-axis */
        raw3f.y = raw2f.y; /* copy Y-axis */

        Vector3f calibrated3f = new Vector3f(raw3f);
        calibration.applyTo(calibrated3f);

        /*
         * The version of applyTo() which accepts a Vector3f behaves exactly
         * the same as the original. As such, it should produce an identical
         * result. However, it should not modify the value of the Z-axis.
         */
        assertEquals(calibrated2f.x, calibrated3f.x);
        assertEquals(calibrated2f.y, calibrated3f.y);
        assertEquals(0.0F, calibrated3f.z);
    }

}
