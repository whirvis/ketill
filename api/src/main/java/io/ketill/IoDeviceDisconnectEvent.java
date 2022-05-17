package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it is disconnected.
 *
 * @see IoDevice#isConnected()
 */
public final class IoDeviceDisconnectEvent extends IoDeviceEvent {

    /**
     * @param device the device which emitted this event.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    IoDeviceDisconnectEvent(@NotNull IoDevice device) {
        super(device);
    }

}
