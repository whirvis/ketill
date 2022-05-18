package io.ketill.controller;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

/**
 * Read-only view of an analog stick's state.
 */
public class StickPos extends ContainerState<StickPosZ> {

    public final @NotNull PressableState up, down, left, right;

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public StickPos(@NotNull StickPosZ internalState) {
        super(internalState);

        this.up = new ButtonState(internalState.up);
        this.down = new ButtonState(internalState.down);
        this.left = new ButtonState(internalState.left);
        this.right = new ButtonState(internalState.right);
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
     * @return the calibration used by {@link #getPos(boolean)}. A value of
     * {@code null} may be returned, and indicates no calibration.
     */
    public @Nullable AnalogStickCalibration getCalibration() {
        return internalState.calibration;
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
     * This method is a shorthand for {@link #getPos(boolean)}, with the
     * argument for {@code calibrate} being {@code true}.
     *
     * @return the analog stick's current position.
     * @see #useCalibration(AnalogStickCalibration)
     */
    public @NotNull Vector3fc getPos() {
        return this.getPos(true);
    }

}
