package io.ketill.hidusb.gc;

import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;
import org.usb4java.Device;

class MockLibUsbDeviceGc extends LibUsbDeviceGc {
    
    MockLibUsbDeviceGc(@NotNull Context context, @NotNull Device usbDevice) {
        super(context, usbDevice);
    }

    void openThing() {
        super.openHandle();
    }
    
}
