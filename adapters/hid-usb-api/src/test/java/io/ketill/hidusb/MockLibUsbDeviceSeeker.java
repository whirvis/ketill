package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.usb4java.Context;

class MockLibUsbDeviceSeeker extends LibUsbDeviceSeeker<IoDevice,
        LibUsbDevice> {

    boolean connectedPeripheral, disconnectedPeripheral;

    public MockLibUsbDeviceSeeker(long scanIntervalMs,
                                  @NotNull Context context,
                                  @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(scanIntervalMs, context, deviceSupplier);
    }

    public MockLibUsbDeviceSeeker(long scanIntervalMs,
                                  @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(scanIntervalMs, deviceSupplier);
    }

    public MockLibUsbDeviceSeeker(@NotNull Context context,
                                  @NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(context, deviceSupplier);
    }

    public MockLibUsbDeviceSeeker(@NotNull LibUsbDeviceSupplier<?> deviceSupplier) {
        super(deviceSupplier);
    }

    @Override
    protected void peripheralConnected(@NotNull LibUsbDevice peripheral) {
        this.connectedPeripheral = true;
    }

    @Override
    protected void peripheralDisconnected(@NotNull LibUsbDevice peripheral) {
        this.disconnectedPeripheral = true;
    }

}
