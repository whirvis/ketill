package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a generic sensor on an {@link IoDevice}.
 * Example of a sensor include (but are not limited to): an accelerometer,
 * a gyroscope, a thermometer, or a barometer.
 */
public final class GenericSensor extends IoFeature<SensorValueZ, SensorValue> {

    /**
     * Constructs a new {@code GenericSensor}.
     * <p>
     * <b>Note:</b> Unlike most other features present in this module, this
     * feature is not restricted to devices of type {@link Controller}.
     *
     * @param id the sensor ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or
     *                                  contains whitespace.
     */
    public GenericSensor(@NotNull String id) {
        super(id);
    }

    @Override
    protected @NotNull SensorValueZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new SensorValueZ();
    }

    @Override
    protected @NotNull SensorValue getContainerState(@NotNull SensorValueZ internalState) {
        return new SensorValue(internalState);
    }

}
