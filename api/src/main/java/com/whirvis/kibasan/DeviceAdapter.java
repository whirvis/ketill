package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

/**
 * Device adapters map data from a source (such as GLFW or X-Input) to an
 * {@link InputDevice}. This allows the same input device to be used with
 * different implementations. As such, device adapters provide portability
 * and a way to enable extra features, such as rumble or gyroscopes.
 * <p/>
 * While not a requirement, it is recommended that each adapter be assigned
 * to a single input device. This makes it easier to map input data.
 *
 * @param <I> the input device type.
 * @see DeviceSeeker
 */
public abstract class DeviceAdapter<I extends InputDevice> {

    /**
     * Called by {@code device} at the end of construction. This is where
     * most, if not all, adapter setup should take place. The registry should
     * be used to map input device features.
     *
     * @param device   the device to initialize.
     * @param registry the feature registry.
     * @see MappedFeatureRegistry#mapFeature(DeviceFeature, Object, StateUpdater)
     */
    /* @formatter:off */
    protected abstract void
            initAdapter(@NotNull I device,
                        @NotNull MappedFeatureRegistry registry);
    /* @formatter:on */

    /**
     * Called by {@code device} when it is polled. This should update the
     * information necessary for mappings to check the current state of their
     * assigned device features.
     *
     * @param device the device being polled.
     */
    protected abstract void pollDevice(@NotNull I device);

    /**
     * Called by {@code device} when its connection status is requested.
     *
     * @param device the device being pinged.
     * @return {@code true} if {@code device} is connected, {@code false}
     * otherwise.
     */
    protected abstract boolean isDeviceConnected(@NotNull I device);

}
