package io.ketill;

import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Provides a mechanism for emitting events from an {@link IoDeviceSeeker}.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @see #onNext(IoDeviceSeekerEvent)
 */
public final class IoDeviceSeekerObserver
        extends EventObserver<IoDeviceSeekerEvent> {

    private final @NotNull IoDeviceSeeker<?> seeker;

    IoDeviceSeekerObserver(@NotNull IoDeviceSeeker<?> seeker,
                           @NotNull Subject<IoDeviceSeekerEvent> subject) {
        super(subject);
        this.seeker = seeker;
    }

    /**
     * Returns the seeker from which this emits events.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
     *
     * @return the seeker from which this emits events.
     */
    public @NotNull IoDeviceSeeker<?> getSeeker() {
        return this.seeker;
    }

    /**
     * Provides subscribers with a new event to observe. This method may
     * be called zero or more times. The event <i>must</i> come from the
     * seeker which created this observer.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
     *
     * @param event the event to emit.
     * @throws NullPointerException     if {@code event} is {@code null}.
     * @throws IllegalArgumentException if {@code event} was constructed
     *                                  to be emitted from a different
     *                                  seeker than the one that created
     *                                  this observer.
     * @see IoDeviceSeekerEvent#getSeeker()
     */
    @Override
    public void onNext(@NotNull IoDeviceSeekerEvent event) {
        Objects.requireNonNull(event, "event cannot be null");

        if (event.getSeeker() != seeker) {
            String msg = "event must be from the device seeker";
            msg += " which created this observer";
            throw new IllegalArgumentException(msg);
        }

        subject.onNext(event);
    }

}
