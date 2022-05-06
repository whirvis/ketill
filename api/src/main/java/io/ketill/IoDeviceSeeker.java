package io.ketill;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The purpose of an I/O device seeker is to scan for I/O devices currently
 * connected to the system. When such a device is detected, the appropriate
 * {@code IoDevice} instance and adapter will be created. Devices must be
 * polled manually after creation using {@link IoDevice#poll()}. This can
 * be done using {@link #pollDevices()}.
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
 * @see #onDiscoverDevice(BiConsumer)
 * @see #onForgetDevice(BiConsumer)
 * @see #onSeekError(BiConsumer)
 * @see IoDeviceAdapter
 */
public abstract class IoDeviceSeeker<I extends IoDevice> implements Closeable {

    private final @NotNull List<I> devices;

    private @Nullable BiConsumer<IoDeviceSeeker<I>, I> discoverDeviceCallback;
    private @Nullable BiConsumer<IoDeviceSeeker<I>, I> forgetDeviceCallback;
    private @Nullable BiConsumer<IoDeviceSeeker<I>, Throwable> errorCallback;

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
    }

    /**
     * Sets the callback for when this seeker has discovered a device. If this
     * callback was set <i>after</i> one or more devices have been discovered,
     * it will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #deviceDiscovered(IoDevice)}. The callback is for users.
     *
     * @param callback the code to execute when a device is discovered. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onDiscoverDevice(@Nullable BiConsumer<IoDeviceSeeker<I>,
            I> callback) {
        this.requireOpen();
        this.discoverDeviceCallback = callback;
    }

    /**
     * Sets the callback for when this seeker has forgotten a device. If this
     * callback was set <i>after</i> one or more devices have been forgotten,
     * it will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #deviceForgotten(IoDevice)}. The callback is for users.
     *
     * @param callback the code to execute when a device is forgotten. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onForgetDevice(@Nullable BiConsumer<IoDeviceSeeker<I>,
            I> callback) {
        this.requireOpen();
        this.forgetDeviceCallback = callback;
    }

    /**
     * Sets the callback for when an error occurs in {@link #seek()}. By
     * default, a wrapping {@code KetillException} will be constructed for
     * the original error and thrown.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #seekerError(Throwable)}. The callback is for users.
     * Furthermore, for the sake of the user, implementing this method will
     * not prevent an exception from being thrown.
     *
     * @param callback the code to execute when an error occurs. A value
     *                 of {@code null} is permitted, and will result in a
     *                 wrapping {@code KetillException} being thrown.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final void onSeekError(@Nullable BiConsumer<IoDeviceSeeker<I>,
            Throwable> callback) {
        this.requireOpen();
        this.errorCallback = callback;
    }

    @MustBeInvokedByOverriders
    protected void discoverDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device cannot be null");
        this.requireOpen();
        if (devices.contains(device)) {
            return;
        }
        devices.add(device);
        this.deviceDiscovered(device);
        if (discoverDeviceCallback != null) {
            discoverDeviceCallback.accept(this, device);
        }
    }

    /**
     * Called when a device is discovered. Overriding this method allows
     * for an I/O device seeker to know when a device has been discovered
     * without needing to set themselves as the callback.
     *
     * @param device the discovered device.
     */
    protected void deviceDiscovered(@NotNull I device) {
        /* optional implement */
    }

    @MustBeInvokedByOverriders
    protected void forgetDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device cannot be null");
        this.requireOpen();
        if (!devices.contains(device)) {
            return;
        }
        devices.remove(device);
        this.deviceForgotten(device);
        if (forgetDeviceCallback != null) {
            forgetDeviceCallback.accept(this, device);
        }
    }

    /**
     * Called when a device is forgotten. Overriding this method allows
     * for an I/O device seeker to know when a device has been forgotten
     * without needing to set themselves as the callback.
     *
     * @param device the forgotten device.
     */
    protected void deviceForgotten(@NotNull I device) {
        /* optional implement */
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
     * @return this device seeker.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     * @throws KetillException       if an error occurs and no callback was
     *                               set via {@link #onSeekError(BiConsumer)}.
     * @see #pollDevices()
     */
    public final synchronized IoDeviceSeeker<I> seek() {
        this.requireOpen();
        try {
            this.seekImpl();
        } catch (Throwable cause) {
            this.seekerError(cause);
            if (errorCallback != null) {
                errorCallback.accept(this, cause);
            } else {
                throw new KetillException("error in DeviceSeeker", cause);
            }
        }
        return this;
    }

    /**
     * Called when an error occurs in {@link #seek()}. Overriding this
     * method allows for an I/O device seeker to know when an error has
     * occurred without needing to set themselves as the callback.
     *
     * @param cause the cause of the error.
     */
    protected void seekerError(@NotNull Throwable cause) {
        /* optional implement */
    }

    /**
     * @return the amount of currently discovered devices.
     */
    public final int getDeviceCount() {
        return devices.size();
    }

    /**
     * Performs the given action for each device discovered by this device
     * seeker until they have all been processed or {@code action} throws
     * an exception. Exceptions thrown by the action are relayed to the
     * caller.
     *
     * @param action the action to perform for each discovered device.
     * @return this device seeker.
     * @throws NullPointerException  if {@code action} is {@code null}.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    /* @formatter:off */
    public final synchronized IoDeviceSeeker<I>
            forEachDevice(@NotNull Consumer<@NotNull I> action) {
        Objects.requireNonNull(action, "action cannot be null");
        this.requireOpen();
        for (I device : devices) {
            action.accept(device);
        }
        return this;
    }
    /* @formatter:on */

    /**
     * Calls {@link IoDevice#poll()} for each discovered device.
     *
     * @return this device seeker.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final synchronized IoDeviceSeeker<I> pollDevices() {
        return this.forEachDevice(IoDevice::poll);
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

        /*
         * This workaround is technically unnecessary since the internal
         * devices list is a CopyOnWriteArrayList. However, using this
         * allows a different type to be used without breaking close().
         */
        while (!devices.isEmpty()) {
            I discovered = devices.get(0);
            this.forgetDevice(discovered);
        }

        this.discoverDeviceCallback = null;
        this.forgetDeviceCallback = null;
        this.errorCallback = null;

        this.closed = true;
    }

}
