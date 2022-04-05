package io.ketill.nx;

import io.ketill.UserUpdatedField;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.Objects;

/**
 * The configuration for an {@link NxCalibration}.
 *
 * @see #applyLs(Vector3f)
 * @see #applyRs(Vector3f)
 */
public final class NxCalibrationConfig {

    /* @formatter:off */
    public static final @NotNull Vector2fc
            LS_UPPER_DEFAULT = new Vector2f(0.70F, 0.70F),
            LS_LOWER_DEFAULT = new Vector2f(-0.70F, -0.70F),
            RS_UPPER_DEFAULT = new Vector2f(0.70F, 0.70F),
            RS_LOWER_DEFAULT = new Vector2f(-0.70F, -0.70F);
    /* @formatter:on */

    private static float normalize(float value, float lower, float upper) {
        value = Math.min(Math.max(value, upper), lower);
        float middle = (lower - upper) / 2.0F;
        return (value - upper - middle) / middle;
    }

    private final @NotNull Vector2f lsUpper, lsLower;
    private final @NotNull Vector2f rsUpper, rsLower;

    /* read-only view of LS and RS bounds */
    @UserUpdatedField
    public final @NotNull Vector2fc lsUpperBounds, lsLowerBounds;
    public final @NotNull Vector2fc rsUpperBounds, rsLowerBounds;

    public NxCalibrationConfig() {
        this.lsUpper = new Vector2f();
        this.lsLower = new Vector2f();
        this.rsUpper = new Vector2f();
        this.rsLower = new Vector2f();

        this.lsUpperBounds = lsUpper;
        this.lsLowerBounds = lsLower;
        this.rsUpperBounds = rsUpper;
        this.rsLowerBounds = rsLower;

        this.setLsBounds(LS_UPPER_DEFAULT, LS_LOWER_DEFAULT);
        this.setRsBounds(RS_UPPER_DEFAULT, RS_LOWER_DEFAULT);
    }

    private void verifyBounds(@NotNull Vector2fc upper,
                              @NotNull Vector2fc lower) {
        Objects.requireNonNull(upper, "upper cannot be null");
        Objects.requireNonNull(lower, "lower cannot be null");
        if (upper.x() <= lower.x() || upper.y() <= lower.y()) {
            throw new IllegalArgumentException("upper bounds must be higher "
                    + "than lower bounds");
        }
    }

    /**
     * Sets the bounds for the left analog stick. The specified bounds
     * are used by {@link #applyLs(Vector3f)} to normalize the position
     * of an analog stick.
     *
     * @throws NullPointerException     if {@code upper} or {@code lower}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if the value of {@code upper}
     *                                  is not higher than the value
     *                                  of {@code lower}.
     */
    @UserUpdatedField
    public void setLsBounds(@NotNull Vector2fc upper,
                            @NotNull Vector2fc lower) {
        this.verifyBounds(upper, lower);
        lsUpper.set(upper);
        lsLower.set(lower);
    }

    /**
     * Applies the calibration configuration for the left analog stick
     * to the specified vector. This method takes a {@code Vector3f}
     * (as opposed to a {@code Vector2f}) as it is the type used by
     * analog sticks to store their current position.
     * <p>
     * <b>Note:</b> This method does <i>not</i> update the Z-axis.
     *
     * @param stick the stick position to apply this calibration to, its
     *              updated value will be on a scale of {@code -1.0F} to
     *              {@code 1.0F}.
     * @throws NullPointerException if {@code stick} is {@code null}.
     * @see #setLsBounds(Vector2fc, Vector2fc)
     */
    @UserUpdatedField
    public void applyLs(@NotNull Vector3f stick) {
        Objects.requireNonNull(stick, "stick cannot be null");
        stick.x = normalize(stick.x, lsUpper.x, lsLower.x);
        stick.y = normalize(stick.y, lsUpper.y, lsLower.y);
    }

    /**
     * Sets the bounds for the right analog stick. The specified bounds
     * are used by {@link #applyRs(Vector3f)} to normalize the position
     * of an analog stick.
     *
     * @throws NullPointerException     if {@code upper} or {@code lower}
     *                                  are {@code null}.
     * @throws IllegalArgumentException if the value of {@code upper}
     *                                  is not higher than the value
     *                                  of {@code lower}.
     */
    @UserUpdatedField
    public void setRsBounds(@NotNull Vector2fc upper,
                            @NotNull Vector2fc lower) {
        this.verifyBounds(upper, lower);
        rsUpper.set(upper);
        rsLower.set(lower);
    }

    /**
     * Applies the calibration configuration for the right analog stick
     * to the specified vector. This method takes a {@code Vector3f}
     * (as opposed to a {@code Vector2f}) as it is the type used by
     * analog sticks to store their current position.
     * <p>
     * <b>Note:</b> This method does <i>not</i> update the Z-axis.
     *
     * @param stick the stick position to apply this calibration to, its
     *              updated value will be on a scale of {@code -1.0F} to
     *              {@code 1.0F}.
     * @throws NullPointerException if {@code stick} is {@code null}.
     * @see #setRsBounds(Vector2fc, Vector2fc)
     */
    @UserUpdatedField
    public void applyRs(Vector3f stick) {
        Objects.requireNonNull(stick, "stick cannot be null");
        stick.x = normalize(stick.x, rsUpper.x, rsLower.x);
        stick.y = normalize(stick.y, rsUpper.y, rsLower.y);
    }

}
