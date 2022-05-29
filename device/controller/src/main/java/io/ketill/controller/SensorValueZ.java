package io.ketill.controller;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Contains the state of a {@link GenericSensor}.
 */
public final class SensorValueZ {

    /**
     * This should be updated by the adapter to store the value
     * of the sensor.
     */
    public final @NotNull Vector3f value;

    SensorValueZ() {
        this.value = new Vector3f();
    }

}
