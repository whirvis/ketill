package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * A supplier of an {@link IoDeviceAdapter}, used by {@link IoDevice} during
 * construction. This is used by the device to initialize the adapter.
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
     * @return the device adapter. This method must <i>never</i> return
     * {@code null}.
     */
    @NotNull IoDeviceAdapter<I> get(@NotNull I device,
                                    @NotNull MappedFeatureRegistry registry);

}
