package io.ketill.controller;

/**
 * Used by {@link AnalogTrigger} and {@link TriggerState} to calibrate
 * the current force of an analog trigger to the appropriate bounds of
 * {@code -1.0F} to {@code 1.0F}.
 *
 * @see #apply(float)
 */
public class AnalogTriggerCalibration {

    public final float upperBound;
    public final float lowerBound;

    /**
     * @param upperBound the upper bound. This <i>must</i> have a value
     *                   greater than {@code lowerBound}.
     * @param lowerBound the lower bound. This <i>must</i> have a value
     *                   lower than {@code upperBound}.
     * @throws IllegalArgumentException if the value of {@code upperBound} is
     *                                  not greater than {@code lowerBound}.
     */
    public AnalogTriggerCalibration(float upperBound, float lowerBound) {
        /*
         * If the upper bound is not greater than the lower bound, it would
         * break normalization. This would render calibration useless.
         */
        if (upperBound <= lowerBound) {
            String msg = "upperBound must be greater than lowerBound";
            throw new IllegalArgumentException(msg);
        }

        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    /**
     * Applies this calibration to the specified value.
     *
     * @param value the value to apply this calibration to. The returned
     *              value will be on a scale of {@code -1.0F} to {@code 1.0F}.
     * @return the calibrated force.
     */
    public float apply(float value) {
        return Analog.normalize(value, upperBound, lowerBound);
    }

}
