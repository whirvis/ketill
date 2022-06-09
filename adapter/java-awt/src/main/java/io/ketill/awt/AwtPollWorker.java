package io.ketill.awt;

import io.ketill.IoDevice;
import io.ketill.ToStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class which calls {@link IoDevice#poll()} on I/O devices in a
 * background thread. Once created, polling of the assigned device
 * can be stopped by calling {@link #close()}.
 * <p>
 * The background thread which polls each device is a single thread.
 * It starts automatically when the first device is added, and stops
 * automatically when no devices are left.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @param <I> the I/O device type.
 * @see #getDevice()
 */
public final class AwtPollWorker<I extends IoDevice> implements Closeable {

    @TestOnly
    static boolean interruptQuitPolling;

    private static final Lock POLL_THREAD_LOCK = new ReentrantLock();
    private static @Nullable AwtPollThread pollThread;

    /* @formatter:off */
    static synchronized <I extends IoDevice> @NotNull AwtPollWorker<I>
            pollInBackground(@NotNull I device) {
        POLL_THREAD_LOCK.lock();
        try {
            /*
             * If pollThread is null, that means no devices were
             * previously being polled. In this situation, create
             * and start a new one.
             */
            if (pollThread == null) {
                pollThread = new AwtPollThread();
                pollThread.running.set(true);
                pollThread.start();
            }

            pollThread.devices.add(device);
        } finally{
            POLL_THREAD_LOCK.unlock();
        }
        return new AwtPollWorker<>(device);
    }
    /* @formatter:on */

    private static synchronized void quitPolling(@NotNull IoDevice device) {
        POLL_THREAD_LOCK.lock();
        try {
            if (pollThread == null) {
                return; /* nothing being polled */
            }

            pollThread.devices.remove(device);

            /*
             * If there are no more devices to poll as a result of
             * calling this method, there is no reason to keep the
             * thread alive. As such, disable it and nullify the
             * reference. If another device is added later, a new
             * thread will be created.
             */
            if (pollThread.devices.isEmpty()) {
                pollThread.running.set(false);

                try {
                    if (interruptQuitPolling) {
                        throw new InterruptedException();
                    }
                    pollThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                pollThread = null;
            }
        } finally {
            POLL_THREAD_LOCK.unlock();
        }
    }

    private final @NotNull I device;
    private final AtomicBoolean closed;

    private AwtPollWorker(@NotNull I device) {
        this.device = device;
        this.closed = new AtomicBoolean();
    }

    /**
     * Returns the I/O device assigned to this worker.
     *
     * @return the I/O device assigned to this worker.
     */
    public @NotNull I getDevice() {
        return this.device;
    }

    /**
     * Returns if this worker has been closed via {@link #close()}.
     *
     * @return {@code true} if this worker has been closed via
     * {@link #close()}, {@code false} otherwise.
     */
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Stops {@link IoDevice#poll()} from being called on the device
     * assigned to this worker. If the worker has already been closed
     * then invoking this method has no effect.
     */
    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            quitPolling(device);
        }
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(this)
                .add("device=" + device)
                .add("closed=" + closed)
                .toString();
    }
    /* @formatter:on */

}
