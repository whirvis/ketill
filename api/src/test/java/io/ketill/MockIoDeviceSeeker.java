package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoDeviceSeeker extends IoDeviceSeeker<MockIoDevice> {

    boolean discoveredDevice;
    boolean forgotDevice;
    Exception seekError;
    boolean invokeSeekInImpl;
    boolean seeked;

    @Override
    protected void deviceDiscovered(@NotNull MockIoDevice device) {
        this.discoveredDevice = true;
    }

    @Override
    protected void deviceForgotten(@NotNull MockIoDevice device) {
        this.forgotDevice = true;
    }

    @Override
    protected void seekImpl() throws Exception {
        if (seekError != null) {
            throw seekError;
        } else if (invokeSeekInImpl) {
            this.seek();
        }
        this.seeked = true;
    }

}
