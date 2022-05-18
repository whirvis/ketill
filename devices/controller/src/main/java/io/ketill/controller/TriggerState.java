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
     * @param calibration the calibration to use when getting the force
     *                    being applied to this analog trigger. A value
     *                    of {@code null} is permitted, and will result
     *                    in no calibration.
     * @see #getForce(boolean)
     */
    public void useCalibration(@Nullable AnalogTriggerCalibration calibration) {
        internalState.calibration = calibration;
    }

    /**
     * @return the calibration used by {@link #getForce(boolean)}. A value
     * of {@code null} may be returned, and indicates no calibration.
     */
    public @Nullable AnalogTriggerCalibration getCalibration() {
        return internalState.calibration;
    }

    /**
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
     * This method is a shorthand for {@link #getForce(boolean)}, with
     * the argument for {@code calibrate} being {@code true}.
     *
     * @return the current force being applied to the analog trigger.
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
