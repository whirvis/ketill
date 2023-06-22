package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The definition for an {@link IoDevice} extension.
 * <p>
 * TODO: documentation
 *
 * @param <D> the I/O device type.
 * @see IoDevice#addExtension(IoExtension)
 */
public abstract class IoExtension<D extends IoDevice> {

    private final @NotNull String id;

    /**
     * Constructs a new {@code IoExtension}.
     *
     * @param id the ID of this I/O extension.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public IoExtension(@NotNull String id) {
        this.id = IoApi.validateId(id);
    }

    /**
     * Returns the ID of this I/O extension.
     *
     * @return the ID of this I/O extension.
     */
    public final @NotNull String getId() {
        return this.id;
    }

    /**
     * Creates a new instance of the I/O extension's device.
     * <p>
     * <b>Requirements</b>
     * <ul>
     *     <li>The returned value must not be {@code null}.</li>
     *     <li>The parent of the created device must be the given parent.
     *     This means a call to {@link IoDevice#getParent()} on the returned
     *     value must return {@code parent}.</li>
     * </ul>
     * <p>
     * If the above requirements are not met, an exception shall be thrown
     * by {@link #createVerifiedDevice(IoDevice)}.
     *
     * @param parent the parent of the I/O extension device.
     * @return the newly created I/O device.
     * @see IoDevice#addExtension(IoExtension)
     */
    protected abstract @NotNull D createDevice(@NotNull IoDevice parent);

    private @NotNull D
    verifyCreatedDevice(@Nullable D device, @NotNull IoDevice parent) {
        if (device == null) {
            String msg = "created device cannot be null";
            throw new IoDeviceException(msg);
        } else if (device.getParent() != parent) {
            String msg = "";
            throw new IoDeviceException(msg);
        }
        return device;
    }

    /**
     * Wrapper for {@link #createDevice(IoDevice)}, which verifies the
     * created device meets the necessary requirements. If they are not
     * met, an {@code IoDeviceException} shall be thrown.
     *
     * @param parent the parent of the I/O extension device.
     * @return the newly created, verified I/O extension device.
     * @throws IoDeviceException if the created device is {@code null};
     *                           if the parent of the created device is
     *                           not {@code parent}.
     */
    protected final @NotNull D createVerifiedDevice(@NotNull IoDevice parent) {
        Objects.requireNonNull(parent, "parent cannot be null");
        D device = this.createDevice(parent);
        return this.verifyCreatedDevice(device, parent);
    }

}