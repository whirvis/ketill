package io.ketill.controller;

import io.ketill.LivingState;
import io.ketill.pressable.MonitorUpdatedField;
import org.jetbrains.annotations.Nullable;

public class TriggerStateZ implements LivingState {

    /**
     * The adapter should only update this to the raw force of the
     * analog trigger (that being, without calibration). Calibration
     * is handled for the adapter by {@link TriggerState}.
     */
    public float force;

    public float calibratedForce;

    public @Nullable AnalogTriggerCalibration calibration;

    /**
     * Adapters should only update the state of {@link #force}. The
     * {@link AnalogTriggerMonitor} handles determining if a trigger
     * is currently pressed or held down.
     */
    @MonitorUpdatedField
    public boolean pressed, held;

    public TriggerStateZ(@Nullable AnalogTriggerCalibration calibration) {
        this.calibration = calibration;
    }

    @Override
    public void update() {
        if (calibration != null) {
            this.calibratedForce = calibration.apply(force);
        } else {
            this.calibratedForce = force;
        }
    }

}
