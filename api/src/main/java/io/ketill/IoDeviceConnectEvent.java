package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it is connected.
 *
 * @see IoDevice#isConnected()
 */
public class IoDeviceConnectEvent extends IoDeviceEvent {

    /**
     * @param device the device which emitted this event.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    IoDeviceConnectEvent(@NotNull IoDevice device) {
        super(device);
    }

}
