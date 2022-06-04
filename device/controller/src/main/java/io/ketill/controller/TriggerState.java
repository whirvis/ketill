package io.ketill.controller;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Read-only view of an analog trigger's state.
 */
public final class TriggerState extends ContainerState<TriggerStateZ>
        implements PressableState {

    TriggerState(@NotNull TriggerStateZ internalState) {
        super(internalState);
    }

    /**
     * Returns the calibration of this analog trigger.
     *
     * @return the calibration of this analog trigger, a value of {@code null}
     * indicates no calibration.
     * @see #getForce(boolean)
     */
    public @Nullable AnalogTriggerCalibration getCalibration() {
        return internalState.calibration;
    }

    /**
     * Updates the calibration of this analog trigger.
     *
     * @param calibration the calibration to use when getting the force
     *                    being applied to this analog trigger. A value
     *                    of {@code null} value is permitted, and will
     *                    result in no calibration.
     * @see #getForce(boolean)
     */
    public void useCalibration(@Nullable AnalogTriggerCalibration calibration) {
        internalState.calibration = calibration;
    }

    /**
     * Returns the analog trigger's current force.
     *
     * @param calibrate {@code true} if the calibrated force should
     *                  be returned, {@code false} for the raw value.
     * @return the analog trigger's current force.
     */
    public float getForce(boolean calibrate) {
        if (!calibrate) {
            return internalState.force;
        } else {
            return internalState.calibratedForce;
        }
    }

    /**
     * Returns the analog trigger's current force.
     * <p>
     * <b>Shorthand for:</b> {@link #getForce(boolean)}, with the argument
     * for {@code calibrate} being {@code true}.
     *
     * @return the analog trigger's current force.
     */
    public float getForce() {
        return this.getForce(true);
    }

    @Override
    public boolean isPressed() {
        return internalState.pressed;
    }

    @Override
    public boolean isHeld() {
        return internalState.held;
    }

}
