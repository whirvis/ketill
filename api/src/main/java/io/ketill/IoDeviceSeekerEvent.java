package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base for events emitted by {@link IoDeviceSeeker}.
 */
public abstract class IoDeviceSeekerEvent {

    private final @NotNull IoDeviceSeeker<?> seeker;

    /**
     * @param seeker the seeker which emitted this event.
     * @throws NullPointerException if {@code seeker} is {@code null}.
     */
    public IoDeviceSeekerEvent(@NotNull IoDeviceSeeker<?> seeker) {
        this.seeker = Objects.requireNonNull(seeker, "seeker cannot be null");
    }

    /**
     * @return the seeker which emitted this event.
     */
    public final @NotNull IoDeviceSeeker<?> getSeeker() {
        return this.seeker;
    }

}
