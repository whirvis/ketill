package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

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
 * @see IoDeviceAdapter
 */
public abstract class IoDeviceSeeker<I extends IoDevice> implements Closeable {

    private final @NotNull Subject<IoDeviceSeekerEvent> subject;

    /**
     * The observer for this I/O device. This should be used to emit events
     * to listeners when they occur.
     * <p>
     * This field is {@code protected} so it is visible to child classes,
     * allowing them to emit their own events from this seeker.
     */
    protected final @NotNull IoDeviceSeekerObserver observer;

    private final @NotNull List<I> devices;

    private boolean closed;

    /**
     * Constructs a new {@code IoDeviceSeeker}.
     */
    public IoDeviceSeeker() {
        this.subject = PublishSubject.create();
        this.observer = new IoDeviceSeekerObserver(this, subject);

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
     * Subscribes to events emitted from this I/O device seeker.
     *
     * @param eventClazz the event type class to listen for. Only events of
     *                   this type and those extending it will be emitted
     *                   to {@code callable}.
     * @param callback   the code to execute when an event of the desired
     *                   type is emitted by the device seeker.
     * @param <T>        the event type.
     * @return the new {@link Disposable} instance, which can be used to
     * dispose the subscription at any time.
     * @throws NullPointerException if {@code eventClazz} or {@code callback}
     *                              are {@code null}.
     */
    /* @formatter:off */
    @SuppressWarnings("unchecked")
    public final <T extends IoDeviceSeekerEvent> @NotNull Disposable
            subscribeEvents(@NotNull Class<T> eventClazz,
                            @NotNull Consumer<T> callback) {
        Objects.requireNonNull(eventClazz, "eventClazz cannot be null");
        Objects.requireNonNull(callback, "callback cannot be null");
        return subject.filter(event -> eventClazz.isAssignableFrom(event.getClass()))
                .map(obj -> (T) obj).subscribe(callback::accept);
    }
    /* @formatter:on */

    /**
     * Subscribes to all events emitted from this I/O device seeker.
     *
     * @param callback the code to execute when an event is emitted by the
     *                 device seeker.
     * @return the new {@link Disposable} instance, which can be used to
     * dispose the subscription at any time.
     * @throws NullPointerException if {@code callback} is {@code null}.
     */
    /* @formatter:off */
    public final @NotNull Disposable
            subscribeEvents(@NotNull Consumer<IoDeviceSeekerEvent> callback) {
        Objects.requireNonNull(callback, "callback cannot be null");
        return this.subscribeEvents(IoDeviceSeekerEvent.class, callback);
    }
    /* @formatter:on */

    /**
     * Discovers a device. If a device is already currently discovered, this
     * method does nothing.
     *
     * @param device the device to discover.
     * @throws NullPointerException  if {@code device} is {@code null}.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     * @see #deviceDiscovered(IoDevice)
     */
    @MustBeInvokedByOverriders
    protected void discoverDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device cannot be null");
        this.requireOpen();
        if (devices.contains(device)) {
            return;
        }
        devices.add(device);
        this.deviceDiscovered(device);
        observer.onNext(new IoDeviceDiscoverEvent(this, device));
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

    /**
     * Forgets a device. If a device is not currently discovered, this
     * method does nothing.
     *
     * @param device the device to forget.
     * @throws NullPointerException  if {@code device} is {@code null}.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     * @see #deviceForgotten(IoDevice)
     */
    @MustBeInvokedByOverriders
    protected void forgetDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device cannot be null");
        this.requireOpen();
        if (!devices.contains(device)) {
            return;
        }
        devices.remove(device);
        this.deviceForgotten(device);
        observer.onNext(new IoDeviceForgetEvent(this, device));
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
     * @throws KetillException       if an error occurs.
     * @see #pollDevices()
     */
    public final synchronized IoDeviceSeeker<I> seek() {
        this.requireOpen();
        try {
            this.seekImpl();
        } catch (Throwable cause) {
            String msg = "error in " + this.getClass().getName();
            throw new KetillException(msg, cause);
        }
        return this;
    }

    /**
     * @return the amount of currently discovered devices.
     * @see #getDevices()
     * @see #forEachDevice(Consumer)
     */
    public final int getDeviceCount() {
        return devices.size();
    }

    /**
     * @return all currently discovered devices.
     * @see #getDeviceCount()
     * @see #forEachDevice(Consumer)
     */
    public final @NotNull List<@NotNull I> getDevices() {
        return Collections.unmodifiableList(devices);
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
     * @see #getDeviceCount()
     * @see #getDevices()
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

    /**
     * @return {@code true} if this I/O device seeker has been closed via
     * {@link #close()}, {@code false} otherwise.
     */
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

        this.closed = true;
    }

}
