package io.ketill.controller;

import io.ketill.pressable.MonitorUpdatedField;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Contains the state of a {@link AnalogStick}.
 */
public class StickPosZ extends Vector3f {

    /**
     * Adapters should only update the position of this vector. The
     * {@link AnalogStickMonitor} handles determining if the button
     * representations for each direction of an analog stick are
     * currently pressed or held down.
     */
    @MonitorUpdatedField
    public final @NotNull ButtonStateZ up, down, left, right;

    public StickPosZ() {
        this.up = new ButtonStateZ();
        this.down = new ButtonStateZ();
        this.left = new ButtonStateZ();
        this.right = new ButtonStateZ();
    }

}
