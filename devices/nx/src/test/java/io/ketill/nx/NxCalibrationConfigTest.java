package io.ketill.nx;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class NxCalibrationConfigTest {

    private NxCalibrationConfig config;

    @BeforeEach
    void setup() {
        this.config = new NxCalibrationConfig();
    }

    @Test
    void setLsBounds() {
        /*
         * It would not make sense to set the upper or lower bounds to a null
         * vector. Assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> config.setLsBounds(null, new Vector2f()));
        assertThrows(NullPointerException.class,
                () -> config.setLsBounds(new Vector2f(), null));

        /*
         * If the value of the upper bound is smaller than the value of
         * the lower bound, the calibration will not be applied correctly.
         * Discard the bounds and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> config.setLsBounds(new Vector2f(1.0F, 1.0F),
                        new Vector2f(1.0F, 0.0F)));
        assertThrows(IllegalArgumentException.class,
                () -> config.setLsBounds(new Vector2f(1.0F, 1.0F),
                        new Vector2f(0.0F, 1.0F)));
    }

    @Test
    void applyLs() {
        /*
         * It would not make sense to apply the calibration for the left
         * analog stick to a null vector. Assume this was a mistake by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> config.applyLs(null));

        /* apply upper bounds for next test */
        Vector3f upperLs = new Vector3f(
                NxCalibrationConfig.LS_UPPER_DEFAULT.x(),
                NxCalibrationConfig.LS_UPPER_DEFAULT.y(), 0.0F);
        config.applyLs(upperLs);

        assertEquals(1.0F, upperLs.x);
        assertEquals(1.0F, upperLs.y);
        assertEquals(0.0F, upperLs.z);

        /* apply lower bounds for next test */
        Vector3f lowerLs = new Vector3f(
                NxCalibrationConfig.LS_LOWER_DEFAULT.x(),
                NxCalibrationConfig.LS_LOWER_DEFAULT.y(), 0.0F);
        config.applyLs(lowerLs);

        assertEquals(-1.0F, lowerLs.x);
        assertEquals(-1.0F, lowerLs.y);
        assertEquals(0.0F, lowerLs.z);
    }

    @Test
    void setRsBounds() {
        /*
         * It would not make sense to set the upper or lower bounds to a null
         * vector. Assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> config.setRsBounds(null, new Vector2f()));
        assertThrows(NullPointerException.class,
                () -> config.setRsBounds(new Vector2f(), null));

        /*
         * If the value of the upper bound is smaller than the value of
         * the lower bound, the calibration will not be applied correctly.
         * Discard the bounds and throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> config.setRsBounds(new Vector2f(1.0F, 1.0F),
                        new Vector2f(1.0F, 0.0F)));
        assertThrows(IllegalArgumentException.class,
                () -> config.setRsBounds(new Vector2f(1.0F, 1.0F),
                        new Vector2f(0.0F, 1.0F)));
    }

    @Test
    void applyRs() {
        /*
         * It would not make sense to apply the calibration for the right
         * analog stick to a null vector. Assume this was a mistake by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> config.applyRs(null));

        /* apply upper bounds for next test */
        Vector3f upperRs = new Vector3f(
                NxCalibrationConfig.RS_UPPER_DEFAULT.x(),
                NxCalibrationConfig.RS_UPPER_DEFAULT.y(), 0.0F);
        config.applyRs(upperRs);

        assertEquals(1.0F, upperRs.x);
        assertEquals(1.0F, upperRs.y);
        assertEquals(0.0F, upperRs.z);

        /* apply lower bounds for next test */
        Vector3f lowerRs = new Vector3f(
                NxCalibrationConfig.RS_LOWER_DEFAULT.x(),
                NxCalibrationConfig.RS_LOWER_DEFAULT.y(), 0.0F);
        config.applyRs(lowerRs);

        assertEquals(-1.0F, lowerRs.x);
        assertEquals(-1.0F, lowerRs.y);
        assertEquals(0.0F, lowerRs.z);
    }

}