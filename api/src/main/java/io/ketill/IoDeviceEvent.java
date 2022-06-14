package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link IoDevice}.
 */
public abstract class IoDeviceEvent extends KetillEvent<IoDevice> {

    /**
     * Constructs a new {@code IoDeviceEvent}.
     *
     * @param emitter the device which emitted this event.
     * @throws NullPointerException if {@code emitter} is {@code null}.
     */
    public IoDeviceEvent(@NotNull IoDevice emitter) {
        super(emitter);
    }

    /**
     * Returns the device which emitted this event.
     * <p>
     * <b>Alias for:</b> {@link #getEmitter()}.
     *
     * @return the device which emitted this event.
     */
    public final @NotNull IoDevice getDevice() {
        return this.getEmitter();
    }

}
