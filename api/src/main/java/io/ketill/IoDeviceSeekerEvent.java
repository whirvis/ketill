package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link IoDeviceSeeker}.
 */
public abstract class IoDeviceSeekerEvent
        extends KetillEvent<IoDeviceSeeker<?>> {

    /**
     * Constructs a new {@code IoDeviceSeekerEvent}.
     *
     * @param emitter the seeker which emitted this event.
     * @throws NullPointerException if {@code emitter} is {@code null}.
     */
    public IoDeviceSeekerEvent(@NotNull IoDeviceSeeker<?> emitter) {
        super(emitter);
    }

    /**
     * Returns the seeker which emitted this event.
     * <p>
     * <b>Alias for:</b> {@link #getEmitter()}.
     *
     * @return the seeker which emitted this event.
     */
    public final @NotNull IoDeviceSeeker<?> getSeeker() {
        return this.getEmitter();
    }

}
