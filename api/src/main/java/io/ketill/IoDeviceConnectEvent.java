package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it is connected.
 *
 * @see IoDevice#isConnected()
 */
public final class IoDeviceConnectEvent extends IoDeviceEvent {

    IoDeviceConnectEvent(@NotNull IoDevice device) {
        super(device);
    }

}
