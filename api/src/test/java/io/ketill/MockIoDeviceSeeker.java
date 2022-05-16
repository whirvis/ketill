package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoDeviceSeeker extends IoDeviceSeeker<MockIoDevice> {

    boolean discoveredDevice;
    boolean forgotDevice;
    boolean errorOnSeek;
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
    protected void seekImpl() {
        if (errorOnSeek) {
            throw new KetillException();
        }
        this.seeked = true;
    }

}
