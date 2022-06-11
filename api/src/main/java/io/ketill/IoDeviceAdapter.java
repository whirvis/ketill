package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Maps data from a source (such as GLFW or X-Input) to the features
 * of an {@link IoDevice}. This allows for the same device to be used
 * with different implementations. This provides portability and ways
 * to enable extra features, like rumble motors or gyroscopes.
 * <p>
 * <b>Thread safety:</b> The thread-safety of an adapter depends on the
 * implementation and underlying APIs. <i>As such, their documentation
 * should be referenced beforehand.</i>
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
     * by the adapter to map I/O features to feature adapter methods.
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
     * Initializes the adapter.
     * <p>
     * This is called before {@link IoDevice#poll()} is invoked
     * for the first time. This is where most, if not all, adapter
     * setup should take place.
     *
     * @see #device
     * @see #registry
     */
    protected abstract void initAdapter();

    /**
     * Called by {@link IoDevice#poll()}.
     * <p>
     * This should update the information necessary for mapped
     * {@link FeatureAdapter} methods to fetch and then update
     * the current state of their assigned features.
     * <p>
     * <b>On error:</b> Any exceptions thrown by this method that
     * are not an instance of {@link KetillException} will be wrapped
     * into one and thrown back to the caller. They will otherwise
     * be thrown to the caller as-is.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void pollDevice() throws Exception;

    /**
     * Called by {@link IoDevice#isConnected()}.
     * <p>
     * <b>Requirements:</b> This <i>must</i> return an up-to-date
     * value with calling {@link #pollDevice()} beforehand.
     *
     * @return {@code true} if {@code device} is connected,
     * {@code false} otherwise.
     */
    protected abstract boolean isDeviceConnected();

}
