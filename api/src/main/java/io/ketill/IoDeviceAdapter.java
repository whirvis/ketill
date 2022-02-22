package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Device adapters map data from a source (such as GLFW or X-Input) to an
 * {@link IoDevice}. This allows the same device to be used with different
 * implementations. This provides portability and a way to enable extra
 * features, such as rumble or gyroscopes.
 *
 * @param <I> the I/O device type.
 * @see AdapterSupplier
 * @see IoDeviceSeeker
 */
public abstract class IoDeviceAdapter<I extends IoDevice> {

    protected final @NotNull I device;
    protected final @NotNull MappedFeatureRegistry registry;

    /**
     * @param device   the device which owns this adapter.
     * @param registry the device's mapped feature registry.
     * @throws NullPointerException if {@code device} or {@code registry}
     *                              are {@code null}.
     */
    public IoDeviceAdapter(@NotNull I device,
                           @NotNull MappedFeatureRegistry registry) {
        this.device = Objects.requireNonNull(device, "device");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    /**
     * Called by {@code device} at the end of construction. This is where
     * most, if not all, adapter setup should take place. The registry should
     * be used give mappings to I/O features. Take note that {@code device}
     * and {@code registry} are fields accessible to the extending class.
     *
     * @see MappedFeatureRegistry#mapFeature(IoFeature, Object, StateUpdater)
     */
    protected abstract void initAdapter();

    /**
     * Called by {@code device} when it is polled. This should update the
     * information necessary for mappings to check the current state of their
     * assigned features.
     * <p>
     * This method can throw any exception without needing to catch it.
     * When an exception is thrown, {@link IoDevice#poll()} wrap it into
     * a {@link KetillException} and throw it to the caller.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void pollDevice() throws Exception;

    /**
     * Called by {@code device} when its connection status is requested.
     *
     * @return {@code true} if {@code device} is connected, {@code false}
     * otherwise.
     */
    protected abstract boolean isDeviceConnected();

}
