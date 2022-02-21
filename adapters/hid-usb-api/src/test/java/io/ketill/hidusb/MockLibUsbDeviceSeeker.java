package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.usb4java.DeviceHandle;

public class MockLibUsbDeviceSeeker extends LibUsbDeviceSeeker<IoDevice> {

    boolean attachedDevice;
    boolean detachedDevice;

    MockLibUsbDeviceSeeker(int scanIntervalMs) {
        super(scanIntervalMs);
    }

    MockLibUsbDeviceSeeker() {
        super();
    }

    @Override
    protected void onDeviceAttach(@NotNull DeviceHandle device) {
        this.attachedDevice = true;
    }

    @Override
    protected void onDeviceDetach(@NotNull DeviceHandle device) {
        this.detachedDevice = true;
    }

}
