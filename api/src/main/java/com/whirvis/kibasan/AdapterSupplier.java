package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

/**
 * A supplier of a {@link DeviceAdapter}, used by {@link InputDevice} during
 * construction. This allows for the device adapter to be initialized by the
 * input device.
 *
 * @param <I> the input device type.
 */
@FunctionalInterface
public interface AdapterSupplier<I extends InputDevice> {

    /**
     * Gets a device adapter.
     *
     * @param device   the device which owns this adapter.
     * @param registry the device's mapped feature registry.
     * @return the device adapter.
     */
    @NotNull DeviceAdapter<I> get(@NotNull I device,
                                  @NotNull MappedFeatureRegistry registry);

}
