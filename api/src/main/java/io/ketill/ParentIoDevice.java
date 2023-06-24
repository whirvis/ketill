package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Container for an extension's parent I/O device.
 * <p>
 * This container exists so only {@link IoDevice} can create new instances
 * of an extension device. This is to prevent invalid device trees from being
 * created.
 *
 * @see IoDevice#addExtension(IoExtension)
 */
public final class ParentIoDevice {

    /**
     * Unwraps a parent's contained I/O device.
     * <p>
     * This method can only be called once to ensure the parent cannot be
     * passed to the constructor of other {@code IoDevice} instances.
     *
     * @return the contained I/O device.
     * @throws NullPointerException  if {@code parent} is {@code null}.
     * @throws IllegalStateException if {@code parent} has been unwrapped.
     */
    static @NotNull IoDevice unwrap(@NotNull ParentIoDevice parent) {
        Objects.requireNonNull(parent, "parent cannot be null");
        return parent.unwrap();
    }

    private final @NotNull IoDevice device;
    private boolean unwrapped;

    /**
     * Constructs a new {@code ParentIoDevice}.
     *
     * @param device the contained I/O device.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    @IoApi.Friends(IoDevice.class)
    ParentIoDevice(@NotNull IoDevice device) {
        this.device = Objects.requireNonNull(device,
                "device cannot be null");
    }

    /**
     * Unwraps the contained I/O device.
     * <p>
     * This method can only be called once to ensure this instance cannot
     * be passed to the constructor of other {@code IoDevice} instances.
     *
     * @return the contained I/O device.
     * @throws IllegalStateException if this parent has been unwrapped.
     */
    private @NotNull IoDevice unwrap() {
        if (unwrapped) {
            throw new IllegalStateException("parent already unwrapped");
        }
        this.unwrapped = true;
        return this.device;
    }

}