package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoDeviceSeekerEvent extends IoDeviceSeekerEvent {

    MockIoDeviceSeekerEvent(@NotNull IoDeviceSeeker<?> seeker) {
        super(seeker);
    }

}
