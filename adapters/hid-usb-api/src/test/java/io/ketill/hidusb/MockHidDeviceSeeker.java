package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

class MockHidDeviceSeeker extends HidDeviceSeeker<IoDevice> {

    boolean connectedDevice;
    boolean disconnectedDevice;

    MockHidDeviceSeeker(int scanIntervalMs) {
        super(scanIntervalMs);
    }

    MockHidDeviceSeeker() {
        super();
    }

    void seekDeviceProduct(HidDevice device) {
        this.seekProduct(device.getVendorId(), device.getProductId());
    }

    @Override
    protected void onDeviceConnect(@NotNull HidDevice device) {
        this.connectedDevice = true;
    }

    @Override
    protected void onDeviceDisconnect(@NotNull HidDevice device) {
        this.disconnectedDevice = true;
    }

}
