package io.ketill.controller;

import io.ketill.LivingState;
import io.ketill.pressable.MonitorUpdatedField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Contains the state of an {@link AnalogStick}.
 */
public class StickPosZ implements LivingState {

    /**
     * The adapter should only update this to the raw position of the
     * analog stick (that being, without calibration). Calibration is
     * handled for the adapter by {@link #update()}.
     */
    public final @NotNull Vector3f pos;

    public @Nullable AnalogStickCalibration calibration;

    public final @NotNull Vector3f calibratedPos;

    /**
     * Adapters should only update the position of this vector. The
     * {@link AnalogStickMonitor} handles determining if the button
     * representations for each direction of an analog stick are
     * currently pressed or held down.
     */
    @MonitorUpdatedField
    public final @NotNull ButtonStateZ up, down, left, right;

    public StickPosZ(@Nullable AnalogStickCalibration calibration) {
        this.pos = new Vector3f();
        this.calibration = calibration;
        this.calibratedPos = new Vector3f();

        this.up = new ButtonStateZ();
        this.down = new ButtonStateZ();
        this.left = new ButtonStateZ();
        this.right = new ButtonStateZ();
    }

    @Override
    public void update() {
        calibratedPos.set(pos);
        if(calibration != null) {
            calibration.applyTo(calibratedPos);
        }
    }
}
