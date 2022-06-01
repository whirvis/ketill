package io.ketill;

import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Provides a mechanism for emitting events from an {@link IoDevice}.
 *
 * @see #onNext(IoDeviceEvent)
 */
public final class IoDeviceObserver extends EventObserver<IoDeviceEvent> {

    private final @NotNull IoDevice device;

    IoDeviceObserver(@NotNull IoDevice device,
                     @NotNull Subject<IoDeviceEvent> subject) {
        super(subject);
        this.device = device;
    }

    /**
     * @return the device from which this emits events.
     */
    public @NotNull IoDevice getDevice() {
        return this.device;
    }

    /**
     * Provides subscribers with a new event to observe. This method may
     * be called 0 or more times. The event <i>must</i> come from the I/O
     * device which created this observer.
     *
     * @param event the event to emit.
     * @throws NullPointerException     if {@code event} is {@code null}.
     * @throws IllegalArgumentException if {@code event} was constructed
     *                                  to be emitted from a different I/O
     *                                  device than the one that created
     *                                  this observer.
     * @see IoDeviceEvent#getDevice()
     */
    @Override
    public void onNext(@NotNull IoDeviceEvent event) {
        Objects.requireNonNull(event, "event cannot be null");

        if (event.getDevice() != device) {
            String msg = "event must be from the device";
            msg += " which created this observer";
            throw new IllegalArgumentException(msg);
        }

        subject.onNext(event);
    }

}
