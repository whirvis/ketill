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

    public float force;

    @AutonomousField
    public @Nullable AnalogTriggerCalibration calibration;

    @AutonomousField
    public float calibratedForce;

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
        if (calibration != null) {
            this.calibratedForce = calibration.apply(force);
        } else {
            this.calibratedForce = force;
        }

        triggerObserver.poll();
    }

}
