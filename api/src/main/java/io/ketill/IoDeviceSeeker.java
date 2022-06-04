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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Scans for I/O devices currently connected to the system.
 * <p>
 * When a device being sought after has been detected, the appropriate
 * {@link IoDevice} and will be instantiated automatically. However,
 * it must be polled manually afterwards. This can be accomplished
 * via the {@link #pollDevices()} helper method.
 * <p>
 * <b>Note:</b> For an I/O device seeker to work as expected, scans must
 * be performed periodically via {@link #seek()}. It is recommended to
 * perform a scan once every application update.
 *
 * @param <I> the I/O device type.
 * @see #discoverDevice(IoDevice)
 * @see #forgetDevice(IoDevice)
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

    private final Lock discoveryLock;
    private final @NotNull List<I> devices;

    private final Lock seekLock;
    private final AtomicBoolean callingSeekImpl;

    private final AtomicBoolean closed;

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
        this.discoveryLock = new ReentrantLock();
        this.devices = new CopyOnWriteArrayList<>();

        this.seekLock = new ReentrantLock();
        this.callingSeekImpl = new AtomicBoolean();

        this.closed = new AtomicBoolean();
    }

    /**
     * Subscribes to events emitted from this I/O device seeker.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
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
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
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

    private void discoverDeviceImpl(@NotNull I device) {
        devices.add(device);
        this.deviceDiscovered(device);
        observer.onNext(new IoDeviceDiscoverEvent(this, device));
    }

    /**
     * Discovers a device. If a device is already currently discovered,
     * this method does nothing.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> A lock
     * is utilized to ensure the internal {@code devices} list cannot be
     * modified by multiple threads at once.
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

        discoveryLock.lock();
        try {
            if (!devices.contains(device)) {
                this.discoverDeviceImpl(device);
            }
        } finally {
            discoveryLock.unlock();
        }
    }

    /**
     * Called when a device is discovered. This will be called before
     * the corresponding event is emitted to subscribers.
     * <p>
     * <b>Note:</b> Invoking {@link #discoverDevice(IoDevice)} inside of
     * this method will likely result in an {@code StackOverflowError}
     * unless proper care is taken to ensure otherwise.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe</i>, assuming
     * that it is called by {@link #discoverDevice(IoDevice)}.
     *
     * @param device the discovered device.
     */
    protected void deviceDiscovered(@NotNull I device) {
        /* optional implement */
    }

    private void forgetDeviceImpl(@NotNull I device) {
        devices.remove(device);
        this.deviceForgotten(device);
        observer.onNext(new IoDeviceForgetEvent(this, device));
    }

    /**
     * Forgets a device. If a device is not currently discovered, this
     * method does nothing.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> A lock
     * is utilized to ensure the internal {@code devices} list cannot be
     * modified by multiple threads at once.
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

        discoveryLock.lock();
        try {
            if (devices.contains(device)) {
                this.forgetDeviceImpl(device);
            }
        } finally {
            discoveryLock.unlock();
        }
    }

    /**
     * Called when a device is forgotten. This will be called before
     * the corresponding event is emitted to subscribers.
     * <p>
     * <b>Note:</b> Invoking {@link #forgetDevice(IoDevice)} inside of
     * this method will likely result in an {@code StackOverflowError}
     * unless proper care is taken to ensure otherwise.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe</i>, assuming
     * that it is called by {@link #forgetDevice(IoDevice)}.
     *
     * @param device the forgotten device.
     */
    protected void deviceForgotten(@NotNull I device) {
        /* optional implement */
    }

    /**
     * Implementation for {@link #seek()}. Invoking {@link #seek()} here
     * will either result in an exception or cause a deadlock on the calling
     * thread <i>which cannot be recovered from.</i>
     * <p>
     * <b>Note:</b> Any exceptions thrown by this method that are not
     * an instance of {@link KetillException} will be wrapped into one
     * and thrown back to the caller.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe</i>,
     * assuming that it is called by {@link #seek()}.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void seekImpl() throws Exception;

    /**
     * Performs a <i>single</i> scan for devices connected to this system.
     * <p>
     * <b>Note:</b> For continuous scanning, this method must be called
     * periodically. It is recommended to call this method once every
     * application update.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe</i>. A lock
     * is utilized to prevent concurrent scans by multiple threads.
     *
     * @return this device seeker. This can be used for chaining method
     * calls, for example: {@code seek().pollDevices()}.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()};
     *                               if {@link #seekImpl()} invokes this
     *                               method.
     * @throws KetillException       if an error occurs.
     * @see #pollDevices()
     */
    public final IoDeviceSeeker<I> seek() {
        this.requireOpen();

        /*
         * Oh, this is bad, this is awful. Calling seek() while it is
         * locked by the calling thread will cause a deadlock! The
         * likely culprit here is a bad implementation of seekImpl().
         */
        if (callingSeekImpl.get()) {
            String msg = "seekImpl() cannot invoke this method";
            msg += " as that would result in an deadlock";
            throw new IllegalStateException(msg);
        }

        seekLock.lock();
        callingSeekImpl.set(true);
        try {
            this.seekImpl();
        } catch (Throwable cause) {
            if (cause instanceof KetillException) {
                throw (KetillException) cause;
            }
            String msg = "error in " + this.getClass().getName();
            throw new KetillException(msg, cause);
        } finally {
            callingSeekImpl.set(false);
            seekLock.unlock();
        }

        return this;
    }

    /**
     * Returns the discovered device count.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
     *
     * @return the discovered device count.
     * @see #getDevices()
     * @see #forEachDevice(Consumer)
     */
    public final int getDeviceCount() {
        return devices.size();
    }

    /**
     * Returns all currently discovered devices.
     * <p>
     * <b>Note:</b> The returned view is wrapped in an unmodifiable list.
     * Attempting to modify it will result in an exception.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe</i>. Iterators
     * of the returned list will not reflect additions, removals, or other
     * such changes since the iterator was created. Furthermore, element
     * changing operations on these iterators ({@code remove}, {@code set},
     * and {@code add}) are not supported.
     *
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
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> Only the
     * devices discovered at the time of invoking this method will be
     * processed. Any modifications to the internal {@code devices} list
     * performed during invocation will not be utilized.
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
    public final IoDeviceSeeker<I>
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
     * <p>
     * <b>Thread safety:</b> This method is equivalent to invoking
     * {@code forEachDevice(IoDevice:poll)}. Since the preceding code
     * is thread-safe, this method is also <i>thread-safe</i>.
     *
     * @return this device seeker.
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    public final IoDeviceSeeker<I> pollDevices() {
        return this.forEachDevice(IoDevice::poll);
    }

    /**
     * Requires that this I/O device seeker not be closed before continuing
     * execution.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> An atomic
     * {@code boolean} is utilized to ensure a cached value is not used when
     * determining if the seeker has been closed.
     *
     * @throws IllegalStateException if this I/O device seeker has been
     *                               closed via {@link #close()}.
     */
    protected final void requireOpen() {
        if (this.isClosed()) {
            throw new IllegalStateException("seeker closed");
        }
    }

    /**
     * Returns if this I/O device seeker has been closed via {@link #close()}.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> An atomic
     * {@code boolean} is utilized to ensure a cached value is not returned.
     *
     * @return {@code true} if this I/O device seeker has been closed via
     * {@link #close()}, {@code false} otherwise.
     */
    public final boolean isClosed() {
        return closed.get();
    }

    /**
     * Closes this I/O device seeker and forgets any previously discovered
     * devices. If the seeker is already closed then invoking this method
     * has no effect.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i> An atomic
     * {@code boolean} and a lock are utilized to ensure the seeker cannot
     * be closed concurrently by multiple threads.
     */
    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return; /* already closed */
        }

        discoveryLock.lock();
        try {
            for (I device : devices) {
                this.forgetDeviceImpl(device);
            }
        } finally {
            discoveryLock.unlock();
        }
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(this)
                .add("closed=" + closed)
                .toString();
    }
    /* @formatter:on */

}
