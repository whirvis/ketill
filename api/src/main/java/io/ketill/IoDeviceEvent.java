package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base for events emitted by {@link IoDevice}.
 */
public abstract class IoDeviceEvent implements IoEvent {

    private final @NotNull IoDevice device;

    /**
     * @param device the device which emitted this event.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    public IoDeviceEvent(@NotNull IoDevice device) {
        this.device = Objects.requireNonNull(device, "device cannot be null");
    }

    @Override
    public final @NotNull IoDevice getDevice() {
        return this.device;
    }

}
