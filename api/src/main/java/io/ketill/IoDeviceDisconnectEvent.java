package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it is disconnected.
 *
 * @see IoDevice#isConnected()
 */
public final class IoDeviceDisconnectEvent extends IoDeviceEvent {

    IoDeviceDisconnectEvent(@NotNull IoDevice device) {
        super(device);
    }

}
