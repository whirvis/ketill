package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * A supplier of an {@link IoDeviceAdapter}, used by {@link IoDevice} during
 * construction. This allows for the adapter to be initialized by the device.
 *
 * @param <I> the I/O device type.
 */
@FunctionalInterface
public interface AdapterSupplier<I extends IoDevice> {

    /**
     * Gets a device adapter.
     *
     * @param device   the device which owns this adapter.
     * @param registry the device's mapped feature registry.
     * @return the device adapter.
     */
    @NotNull IoDeviceAdapter<I> get(@NotNull I device,
                                    @NotNull MappedFeatureRegistry registry);

}
