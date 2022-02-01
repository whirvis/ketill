package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Device adapters map data from a source (such as GLFW or X-Input) to an
 * {@link InputDevice}. This allows the same input device to be used with
 * different implementations. As such, device adapters provide portability
 * and a way to enable extra features, such as rumble or gyroscopes.
 *
 * @param <I> the input device type.
 * @see AdapterSupplier
 * @see DeviceSeeker
 */
public abstract class DeviceAdapter<I extends InputDevice> {

    protected final @NotNull I device;
    protected final @NotNull MappedFeatureRegistry registry;

    /**
     * @param device   the device which owns this adapter.
     * @param registry the device's mapped feature registry.
     */
    public DeviceAdapter(@NotNull I device,
                         @NotNull MappedFeatureRegistry registry) {
        this.device = Objects.requireNonNull(device, "device");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    /**
     * Called by {@code device} at the end of construction. This is where
     * most, if not all, adapter setup should take place. The registry should
     * be used to map input device features. Take note that {@code device}
     * and {@code registry} are accessible fields to the extending class.
     *
     * @see MappedFeatureRegistry#mapFeature(DeviceFeature, Object, StateUpdater)
     */
    protected abstract void initAdapter();

    /**
     * Called by {@code device} when it is polled. This should update the
     * information necessary for mappings to check the current state of their
     * assigned device features.
     */
    protected abstract void pollDevice();

    /**
     * Called by {@code device} when its connection status is requested.
     *
     * @return {@code true} if {@code device} is connected, {@code false}
     * otherwise.
     */
    protected abstract boolean isDeviceConnected();

}
