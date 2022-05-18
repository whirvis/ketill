package io.ketill.controller;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Contains the state of an {@link AnalogTrigger}.
 */
public class TriggerStateZ implements AutonomousState {

    public float force;

    @AutonomousField
    public @Nullable AnalogTriggerCalibration calibration;

    @AutonomousField
    public float calibratedForce;

    @AutonomousField
    public boolean pressed, held;

    private final AnalogTriggerObserver triggerObserver;

    /**
     * @param trigger     the analog trigger which created this state.
     * @param observer    the I/O device observer.
     * @param calibration the initial calibration.
     * @throws NullPointerException if {@code trigger} or {@code observer}
     *                              are {@code null}.
     */
    public TriggerStateZ(@NotNull AnalogTrigger trigger,
                         @NotNull IoDeviceObserver observer,
                         @Nullable AnalogTriggerCalibration calibration) {
        Objects.requireNonNull(trigger, "trigger cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");

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
