package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a battery inside an {@link IoDevice}.
 * Although most devices have only one battery, it is technically
 * possible for an I/O device to have multiple.
 */
public final class InternalBattery
        extends IoFeature<BatteryLevelZ, BatteryLevel> {

    /**
     * Constructs a new {@code InternalBattery}.
     * <p>
     * <b>Note:</b> Unlike most other features present in this module, this
     * feature is not restricted to devices of type {@link Controller}.
     *
     * @param id the battery ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or
     *                                  contains whitespace.
     */
    public InternalBattery(@NotNull String id) {
        super(id);
    }

    @Override
    protected @NotNull BatteryLevelZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new BatteryLevelZ();
    }

    @Override
    protected @NotNull BatteryLevel getContainerState(@NotNull BatteryLevelZ internalState) {
        return new BatteryLevel(internalState);
    }

}
