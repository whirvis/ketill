package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when its {@link IoDeviceAdapter} has been
 * initialized.
 */
public final class AdapterInitializedEvent extends IoDeviceEvent {

    /**
     * Constructs a new {@code AdapterInitializedEvent}.
     *
     * @param device the device which emitted this event.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    public AdapterInitializedEvent(@NotNull IoDevice device) {
        super(device);
    }

}
