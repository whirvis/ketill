package io.ketill.controller;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * Used by {@link AnalogStick} and {@link StickPos} to calibrate the current
 * position of an analog stick to the appropriate bounds of {@code -1.0F} to
 * {@code 1.0F}.
 *
 * @see #applyTo(Vector2f)
 * @see #applyTo(Vector3f)
 */
public final class AnalogStickCalibration {

    private static float normalize(float value, float upper, float lower) {
        float normalized = (value - lower) / (upper - lower);
        return (normalized * 2.0F) - 1.0F;
    }

    public final @NotNull Vector2fc upperBound;
    public final @NotNull Vector2fc lowerBound;

    /**
     * @param upperBound the upper bound. This <i>must</i> have a value
     *                   greater than {@code lowerBound}.
     *                   <b>Note:</b> Only the current value of this vector
     *                   is used! If the vector is updated, it will not be
     *                   reflected in the bounds of this calibration.
     * @param lowerBound the lower bound. This <i>must</i> have a value
     *                   lower than {@code upperBound}.
     *                   <b>Note:</b> Only the current value of this vector
     *                   is used! If the vector is updated, it will not be
     *                   reflected in the bounds of this calibration.
     * @throws NullPointerException     if {@code upperBound} or
     *                                  {@code lowerBound} are {@code null}.
     * @throws IllegalArgumentException if the value of {@code upperBound} is
     *                                  not greater than {@code lowerBound}.
     */
    public AnalogStickCalibration(@NotNull Vector2fc upperBound,
                                  @NotNull Vector2fc lowerBound) {
        Objects.requireNonNull(upperBound, "upperBound cannot be null");
        Objects.requireNonNull(lowerBound, "lowerBound cannot be null");

        /*
         * If the upper bound is not greater than the lower bound, it would
         * break normalization. This would render calibration useless.
         */
        if (upperBound.x() <= lowerBound.x() || upperBound.y() <= lowerBound.y()) {
            String msg = "upperBound must be greater than lowerBound";
            throw new IllegalArgumentException(msg);
        }

        this.upperBound = new Vector2f(upperBound);
        this.lowerBound = new Vector2f(lowerBound);
    }

    /**
     * @param upperBoundX the X-axis value for the upper bound. This
     *                    <i>must</i> be greater than {@code lowerBoundX}.
     * @param upperBoundY the Y-axis value for the upper bound. This
     *                    <i>must</i> be greater than {@code lowerBoundY}.
     * @param lowerBoundX the X-axis value for the lower bound. This
     *                    <i>must</i> be lower than {@code upperBoundX}.
     * @param lowerBoundY the Y-axis value for the lower bound. This
     *                    <i>must</i> be lower than {@code upperBoundY}.
     * @throws IllegalArgumentException if the value of {@code upperBoundX}
     *                                  is less than {@code lowerBoundX};
     *                                  if the value of {@code upperBoundY}
     *                                  is less than {@code lowerBoundY}.
     */
    public AnalogStickCalibration(float upperBoundX, float upperBoundY,
                                  float lowerBoundX, float lowerBoundY) {
        /* @formatter:off */
        this(new Vector2f(upperBoundX, upperBoundY),
                new Vector2f(lowerBoundX, lowerBoundY));
        /* @formatter:on */
    }

    /**
     * Applies this calibration to the specified vector.
     *
     * @param vec the vector to apply this calibration to. The updated
     *            values of each axis will be on a scale of {@code -1.0F}
     *            to {@code 1.0F}.
     * @throws NullPointerException if {@code vec} is {@code null}.
     * @see #applyTo(Vector3f)
     */
    public void applyTo(@NotNull Vector2f vec) {
        Objects.requireNonNull(vec, "vec cannot be null");
        vec.x = normalize(vec.x, upperBound.x(), lowerBound.x());
        vec.y = normalize(vec.y, upperBound.y(), lowerBound.y());
    }

    /**
     * Applies this calibration to the specified vector.
     * <p>
     * <b>Note:</b> This method does <i>not</i> modify the Z-axis.
     *
     * @param vec the vector to apply this calibration to. The updated
     *            values of each axis will be on a scale of {@code -1.0F}
     *            to {@code 1.0F}.
     * @throws NullPointerException if {@code vec} is {@code null}.
     * @see #applyTo(Vector2f)
     */
    public void applyTo(@NotNull Vector3f vec) {
        Objects.requireNonNull(vec, "vec cannot be null");
        vec.x = normalize(vec.x, upperBound.x(), lowerBound.x());
        vec.y = normalize(vec.y, upperBound.y(), lowerBound.y());
    }

}
