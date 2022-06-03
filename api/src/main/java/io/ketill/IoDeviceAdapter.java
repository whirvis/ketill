package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Maps data from a source (such as GLFW or X-Input) to the features of
 * an {@link IoDevice}. This allows for the same device to be used with
 * different implementations. This provides both portability and a way to
 * enable extra features like rumble motors or gyroscopes.
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
     * Called before {@code device} is polled for the first time.
     * This is where most, if not all, adapter setup should take place.
     *
     * @see #device
     * @see #registry
     */
    protected abstract void initAdapter();

    /**
     * Called by {@code device} each time it is polled.
     * <p>
     * This should update the information necessary for mapped
     * {@link FeatureAdapter} methods to fetch and then update
     * the current state of their assigned features.
     * <p>
     * <b>Note:</b> Any exceptions thrown by this method that are
     * not an instance of {@link KetillException} will be wrapped
     * into one and thrown back to the caller.
     *
     * @throws Exception if an error occurs.
     * @see IoDevice#poll()
     */
    protected abstract void pollDevice() throws Exception;

    /**
     * Called by {@link IoDevice#isConnected()}.
     * <p>
     * <b>Note:</b> This <i>must</i> return an up-to-date value without
     * a call to {@link #pollDevice()} being necessary beforehand.
     *
     * @return {@code true} if {@code device} is connected, {@code false}
     * otherwise.
     */
    protected abstract boolean isDeviceConnected();

}
