package io.ketill.controller;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains the state of an {@link AnalogTrigger}.
 */
public final class TriggerStateZ implements AutonomousState {

    /**
     * This should be updated by the adapter to store the <i>raw</i>
     * position of the analog trigger.
     */
    public float force;

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is used by the state to apply calibration, and is set by
     * the user of their own accord.
     */
    @AutonomousField
    public @Nullable AnalogTriggerCalibration calibration;

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is updated automatically by the state.
     *
     * @see #force
     */
    @AutonomousField
    public float calibratedForce;

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is updated automatically by the state.
     *
     * @see #force
     */
    @AutonomousField
    public boolean pressed, held;

    private final AnalogTriggerObserver triggerObserver;

    TriggerStateZ(@NotNull AnalogTrigger trigger,
                  @NotNull IoDeviceObserver observer,
                  @Nullable AnalogTriggerCalibration calibration) {
        this.calibration = calibration;

        this.triggerObserver = new AnalogTriggerObserver(trigger,
                this, observer);
    }

    @Override
    public void update() {
        this.calibratedForce = force;
        if (calibration != null) {
            this.calibratedForce = calibration.apply(force);
        }
        triggerObserver.poll();
    }

}
