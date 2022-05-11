package io.ketill.awt;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class which calls {@link IoDevice#poll()} on I/O devices in a background
 * thread.
 *
 * @param <I> the I/O device type.
 * @see #getDevice()
 * @see #cancel()
 */
public final class AwtPollWorker<I extends IoDevice> {

    private static @Nullable AwtPollThread pollThread;

    /* @formatter:off */
    static synchronized <I extends IoDevice> @NotNull AwtPollWorker<I>
            pollInBackground(@NotNull I device) {
        /*
         * If pollThread is null, that means no devices were previously being
         * polled. In this situation, just create a new one and start it.
         */
        if (pollThread == null) {
            pollThread = new AwtPollThread();
            pollThread.start();
        }

        synchronized (pollThread.devices) {
            pollThread.devices.add(device);
        }

        return new AwtPollWorker<>(device);
    }
    /* @formatter:on */

    private static synchronized void quitPolling(@NotNull IoDevice device) {
        if (pollThread == null) {
            return; /* nothing being polled */
        }

        synchronized (pollThread.devices) {
            pollThread.devices.remove(device);

            /*
             * If there are no more devices to poll as a result of calling
             * this method, there is no reason to keep the thread alive.
             * As such, disable it and nullify the reference. If another
             * device is added later, a new thread will be created.
             */
            if (pollThread.devices.isEmpty()) {
                pollThread.interrupt();
                pollThread = null;
            }
        }
    }

    private final @NotNull I device;
    private boolean cancelled;

    private AwtPollWorker(@NotNull I device) {
        this.device = device;
    }

    /**
     * @return the I/O device this worker was assigned to poll.
     */
    public @NotNull I getDevice() {
        return this.device;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Stops {@link IoDevice#poll()} from being called on the I/O device
     * assigned to this worker. If the worker has already been cancelled
     * then invoking this method has no effect.
     */
    public void cancel() {
        if (this.isCancelled()) {
            return;
        }
        quitPolling(device);
        this.cancelled = true;
    }

}
