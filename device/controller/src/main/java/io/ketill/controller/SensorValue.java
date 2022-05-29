package io.ketill.controller;

import io.ketill.ContainerState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

/**
 * Read-only view of a sensor's current value.
 */
public final class SensorValue extends ContainerState<SensorValueZ> {

    SensorValue(SensorValueZ internalState) {
        super(internalState);
    }

    /**
     * @return the sensor's current value.
     */
    public @NotNull Vector3fc getValue() {
        return internalState.value;
    }

    /**
     * <b>Shorthand for:</b> {@code getValue().x()}
     *
     * @return the X-axis of the sensor's current value.
     */
    public float getX() {
        return this.getValue().x();
    }

    /**
     * <b>Shorthand for:</b> {@code getValue().y()}
     *
     * @return the Y-axis of the sensor's current value.
     */
    public float getY() {
        return this.getValue().y();
    }

    /**
     * <b>Shorthand for:</b> {@code getValue().z()}
     *
     * @return the Z-axis of the sensor's current value.
     */
    public float getZ() {
        return this.getValue().z();
    }

}
