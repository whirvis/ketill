package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoDeviceEvent extends IoDeviceEvent {

    MockIoDeviceEvent(@NotNull IoDevice device) {
        super(device);
    }

}
