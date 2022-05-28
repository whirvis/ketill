package io.ketill.controller;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

/**
 * Read-only view of an analog stick's state.
 */
public final class StickPos extends ContainerState<StickPosZ> {

    /**
     * Representations of each {@link Direction} the analog stick can be
     * pressed towards.
     * <p>
     * For example: if {@code up.isPressed()} returns {@code true}, that
     * means the position of this analog stick is pressed towards
     * {@link Direction#UP}.
     */
    public final @NotNull PressableState up, down, left, right;

    StickPos(@NotNull StickPosZ internalState) {
        super(internalState);

        this.up = new ButtonState(internalState.up);
        this.down = new ButtonState(internalState.down);
        this.left = new ButtonState(internalState.left);
        this.right = new ButtonState(internalState.right);
    }

    /**
     * @return the calibration used by {@link #getPos(boolean)}. A value of
     * {@code null} may be returned, and indicates no calibration.
     */
    public @Nullable AnalogStickCalibration getCalibration() {
        return internalState.calibration;
    }

    /**
     * @param calibration the calibration to use when getting the position
     *                    of this analog stick. A value of {@code null} is
     *                    permitted, and will result in no calibration.
     * @see #getPos(boolean)
     */
    public void useCalibration(@Nullable AnalogStickCalibration calibration) {
        internalState.calibration = calibration;
    }

    /**
     * @param calibrate {@code true} if the calibrated position should
     *                  be returned, {@code false} for the raw value.
     * @return the analog stick's current position.
     * @see #useCalibration(AnalogStickCalibration)
     */
    public @NotNull Vector3fc getPos(boolean calibrate) {
        if (!calibrate) {
            return internalState.pos;
        } else {
            return internalState.calibratedPos;
        }
    }

    /**
     * <b>Shorthand for:</b> {@link #getPos(boolean)}, with the argument for
     * {@code calibrate} being {@code true}.
     *
     * @return the analog stick's current position.
     * @see #useCalibration(AnalogStickCalibration)
     */
    public @NotNull Vector3fc getPos() {
        return this.getPos(true);
    }

    /**
     * <b>Shorthand for:</b> {@code getPos(calibrate).x()}
     *
     * @param calibrate {@code true} if the calibrated position should
     *                  be returned, {@code false} for the raw value.
     * @return the X-axis of the analog stick's current position.
     * @see #getPos(boolean)
     */
    public float getX(boolean calibrate) {
        return this.getPos(calibrate).x();
    }

    /**
     * <b>Shorthand for:</b> {@link #getX(boolean)}, with the argument for
     * {@code calibrate} being {@code true}.
     *
     * @return the X-axis of the analog stick's current position.
     */
    public float getX() {
        return this.getX(true);
    }

    /**
     * <b>Shorthand for:</b> {@code getPos(calibrate).y()}
     *
     * @param calibrate {@code true} if the calibrated position should
     *                  be returned, {@code false} for the raw value.
     * @return the Y-axis of the analog stick's current position.
     * @see #getPos(boolean)
     */
    public float getY(boolean calibrate) {
        return this.getPos(calibrate).y();
    }

    /**
     * <b>Shorthand for:</b> {@link #getY(boolean)}, with the argument for
     * {@code calibrate} being {@code true}.
     *
     * @return the Y-axis of the analog stick's current position.
     */
    public float getY() {
        return this.getY(true);
    }

    /**
     * <b>Shorthand for:</b> {@code getPos(calibrate).z()}
     *
     * @param calibrate {@code true} if the calibrated position should
     *                  be returned, {@code false} for the raw value.
     * @return the Z-axis of the analog stick's current position.
     * @see #getPos(boolean)
     */
    public float getZ(boolean calibrate) {
        return this.getPos(calibrate).z();
    }

    /**
     * <b>Shorthand for:</b> {@link #getZ(boolean)}, with the argument for
     * {@code calibrate} being {@code true}.
     *
     * @return the Z-axis of the analog stick's current position.
     */
    public float getZ() {
        return this.getZ(true);
    }

}
