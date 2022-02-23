package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

class MockLibUsbDeviceSeeker
        extends LibUsbDeviceSeeker<IoDevice, LibUsbDevice> {

    boolean blacklistedDevice;
    int deviceScanCount;
    boolean errorOnDeviceConnect;
    boolean errorOnDeviceDisconnect;
    boolean connectedDevice;
    boolean disconnectedDevice;

    MockLibUsbDeviceSeeker(int scanIntervalMs) {
        super(scanIntervalMs, LibUsbDevice::new);
    }

    MockLibUsbDeviceSeeker() {
        super(LibUsbDevice::new);
    }

    void seekDeviceProduct(LibUsbDevice device) {
        this.seekProduct(device.getVendorId(), device.getProductId());
    }

    @Override
    protected void blacklistDevice(@NotNull LibUsbDevice device) {
        super.blacklistDevice(device);
        this.blacklistedDevice = true;
    }

    @Override
    void scanDevices() {
        super.scanDevices();
        this.deviceScanCount++;
    }

    @Override
    protected void onDeviceConnect(@NotNull LibUsbDevice device) {
        if(errorOnDeviceConnect) {
            throw new RuntimeException();
        }
        this.connectedDevice = true;
    }

    @Override
    protected void onDeviceDisconnect(@NotNull LibUsbDevice device) {
        if(errorOnDeviceDisconnect) {
            throw new RuntimeException();
        }
        this.disconnectedDevice = true;
    }

}
