package io.ketill;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The purpose of an I/O device seeker is to scan for I/O devices currently
 * connected to the system. When such a device is detected, the appropriate
 * {@code IoDevice} instance and adapter will be created. Devices must be
 * polled manually after creation using {@link IoDevice#poll()}. They can
 * be retrieved from {@link #discoveredDevices}.
 * <p>
 * Implementations should call {@link #discoverDevice(IoDevice)} when a
 * device is discovered and {@link #forgetDevice(IoDevice)} when a device
 * is forgotten.
 * <p>
 * <b>Note:</b> For a I/O device seeker to work as expected, scans must be
 * performed periodically via {@link #seek()}. It is recommended to perform
 * a scan once every application update.
 *
 * @param <I> the I/O device type.
 * @see #onDiscoverDevice(Consumer)
 * @see #onForgetDevice(Consumer)
 * @see #onSeekError(Consumer)
 * @see IoDeviceAdapter
 */
public abstract class IoDeviceSeeker<I extends IoDevice> implements Closeable {

    private final @NotNull List<I> devices;
    public final @NotNull List<I> discoveredDevices; /* read only view */

    private @Nullable Consumer<? super I> discoverDeviceCallback;
    private @Nullable Consumer<? super I> forgetDeviceCallback;
    private @Nullable Consumer<Throwable> errorCallback;

    private boolean closed;

    public IoDeviceSeeker() {
        /*
         * Its possible devices will be modified while being iterated
         * over (e.g., a device is discovered or forgotten.) Using a
         * traditional ArrayList, this would result in an exception.
         *
         * Modifying a CopyOnWriteArrayList has quite some overhead.
         * However, this list in particular is modified only when a
         * device is discovered or forgotten. This (usually) occurs
         * only a few times throughout the lifetime of the program.
         * As such, the overhead is negligible.
         */
        this.devices = new CopyOnWriteArrayList<>();
        this.discoveredDevices = Collections.unmodifiableList(devices);
    }

    /**
     * Sets the callback for when this seeker has discovered a device. If this
     * callback was set <i>after</i> one or more devices have been discovered,
     * it will not be called for them. Current devices will have to be
     * retrieved via {@link #discoveredDevices}.
     *
     * @param callback the code to execute when a device is discovered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onDiscoverDevice(@Nullable Consumer<? super I> callback) {
        this.requireOpen();
        this.discoverDeviceCallback = callback;
    }

    /**
     * Sets the callback for when this seeker has forgotten a device. If this
     * callback was set <i>after</i> one or more devices have been forgotten,
     * it will not be called for them. Current devices will have to be
     * retrieved via {@link #discoveredDevices}.
     *
     * @param callback the code to execute when a device is forgotten. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onForgetDevice(@Nullable Consumer<? super I> callback) {
        this.requireOpen();
        this.forgetDeviceCallback = callback;
    }

    /**
     * Sets the callback for when an error occurs in {@link #seek()}. By
     * default, a wrapping {@code KetillException} will be constructed for
     * the original error and thrown.
     *
     * @param callback the code to execute when an error occurs. A value
     *                 of {@code null} is permitted, and will result in a
     *                 wrapping {@code KetillException} being thrown.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onSeekError(@Nullable Consumer<Throwable> callback) {
        this.requireOpen();
        this.errorCallback = callback;
    }

    @MustBeInvokedByOverriders
    protected void discoverDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device");
        this.requireOpen();
        if (devices.contains(device)) {
            return;
        }
        devices.add(device);
        if (discoverDeviceCallback != null) {
            discoverDeviceCallback.accept(device);
        }
    }

    @MustBeInvokedByOverriders
    protected void forgetDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device");
        this.requireOpen();
        if (!devices.contains(device)) {
            return;
        }
        devices.remove(device);
        if (forgetDeviceCallback != null) {
            forgetDeviceCallback.accept(device);
        }
    }

    /**
     * Called by {@link #seek()}, this method can throw any exception without
     * needing to catch it. When an exception is thrown, {@link #seek()} will
     * wrap it into a {@link KetillException} and throw it to the caller.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void seekImpl() throws Exception;

    /**
     * Performs a <i>single</i> scan for devices connected to this system.
     * For continuous scanning, this method must be called periodically once
     * every application update.
     *
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     * @throws KetillException       if an error occurs and no callback was
     *                               set via {@link #onSeekError(Consumer)}.
     */
    public final synchronized void seek() {
        this.requireOpen();
        try {
            this.seekImpl();
        } catch (Throwable cause) {
            if (errorCallback != null) {
                errorCallback.accept(cause);
            } else {
                throw new KetillException("error in DeviceSeeker", cause);
            }
        }
    }

    /**
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    protected final void requireOpen() {
        if (closed) {
            throw new IllegalStateException("seeker closed");
        }
    }

    public final boolean isClosed() {
        return this.closed;
    }

    /**
     * Closes this I/O device seeker and forgets any previously discovered
     * devices. If the seeker is already closed then invoking this method
     * has no effect.
     */
    @Override
    @MustBeInvokedByOverriders
    public synchronized void close() {
        if (closed) {
            return;
        }

        for (I discovered : devices) {
            this.forgetDevice(discovered);
        }

        this.discoverDeviceCallback = null;
        this.forgetDeviceCallback = null;
        this.errorCallback = null;

        this.closed = true;
    }

}
