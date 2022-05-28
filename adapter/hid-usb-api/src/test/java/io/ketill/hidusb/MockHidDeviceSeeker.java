package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;

class MockHidDeviceSeeker extends HidDeviceSeeker<IoDevice> {

    boolean connectedPeripheral, disconnectedPeripheral;

    public MockHidDeviceSeeker(long scanIntervalMs) {
        super(scanIntervalMs);
    }

    public MockHidDeviceSeeker() {
        super();
    }

    @Override
    protected void peripheralConnected(@NotNull HidDevice peripheral) {
        this.connectedPeripheral = true;
    }

    @Override
    protected void peripheralDisconnected(@NotNull HidDevice peripheral) {
        this.disconnectedPeripheral = true;
    }

}
