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
 * @see FeatureAdapter
 * @see MappingMethod
 * @see MappingType
 */
public abstract class IoDeviceAdapter<I extends IoDevice> {

    /**
     * The I/O device which owns this adapter.
     *
     * @see #registry
     */
    protected final @NotNull I device;

    /**
     * The mapped feature registry of {@link #device}. This should be used
     * by the adapter to map existing features to feature adapter methods.
     */
    protected final @NotNull MappedFeatureRegistry registry;

    /**
     * Constructs a new {@code IoDeviceAdapter}.
     *
     * @param device   the device which owns this adapter.
     * @param registry the device's mapped feature registry.
     * @throws NullPointerException if {@code device} or {@code registry}
     *                              are {@code null}.
     */
    public IoDeviceAdapter(@NotNull I device,
                           @NotNull MappedFeatureRegistry registry) {
        this.device = Objects.requireNonNull(device,
                "device cannot be null");
        this.registry = Objects.requireNonNull(registry,
                "registry cannot be null");
    }

    /**
     * Called before {@link IoDevice#poll()} is called for the first time.
     * This is where most, if not all, adapter setup should take place.
     * The feature registry should be used give mappings to I/O features.
     *
     * @see #device
     * @see #registry
     */
    protected abstract void initAdapter();

    /**
     * Called by {@code device} when it is polled. This should update
     * the information necessary for mappings to check the current state
     * of their assigned features.
     * <p>
     * Any exceptions thrown by this method will be wrapped into a
     * {@link KetillException} and thrown back to the caller.
     * Take note that if the exception is a {@link KetillException}
     * instance, it will be thrown as-is without a wrapper.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void pollDevice() throws Exception;

    /**
     * Called by {@code device} when its connection status is requested
     * via {@link IoDevice#isConnected()}.
     *
     * @return {@code true} if {@code device} is connected, {@code false}
     * otherwise.
     */
    protected abstract boolean isDeviceConnected();

}
