package io.ketill.hidusb;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class MockPeripheralSeeker extends PeripheralSeeker<IoDevice, MockPeripheral> {

    private static void requireScanWaitDisabled() {
        if (!scanWaitDisabled) {
            String msg = "scanWaitDisabled must equal true";
            throw new IllegalStateException(msg);
        }
    }

    private final List<MockPeripheral> attached;

    boolean targetedProduct, droppedProduct;
    boolean blockedPeripheral, unblockedPeripheral;
    boolean attachedPeripheral, detachedPeripheral;
    boolean errorOnAttach, errorOnDetach;
    boolean failedAttach, failedDetach;
    boolean connectedPeripheral, disconnectedPeripheral;

    int peripheralScanCount;

    MockPeripheralSeeker(long scanIntervalMs) {
        super(scanIntervalMs);
        this.attached = new ArrayList<>();
    }

    MockPeripheralSeeker() {
        super();
        this.attached = new ArrayList<>();
    }

    void attachMock(@NotNull MockPeripheral peripheral) {
        requireScanWaitDisabled();
        if (!attached.contains(peripheral)) {
            attached.add(peripheral);
            this.seek(); /* scan for peripherals */
        }
    }

    void detachMock(@NotNull MockPeripheral peripheral) {
        requireScanWaitDisabled();
        if (attached.contains(peripheral)) {
            attached.remove(peripheral);
            this.seek(); /* scan for peripherals */
        }
    }

    @Override
    protected @NotNull ProductId getId(@NotNull MockPeripheral peripheral) {
        return peripheral.id;
    }

    @Override
    protected int getHash(@NotNull MockPeripheral peripheral) {
        return peripheral.hashCode();
    }

    @Override
    protected @NotNull Collection<@NotNull MockPeripheral> scanPeripherals() {
        this.peripheralScanCount++;
        return Collections.unmodifiableList(attached);
    }

    @Override
    protected void productTargeted(@NotNull ProductId id) {
        this.targetedProduct = true;
    }

    @Override
    protected void productDropped(@NotNull ProductId id) {
        this.droppedProduct = true;
    }

    @Override
    protected void peripheralBlocked(@NotNull BlockedPeripheral<MockPeripheral> peripheral) {
        this.blockedPeripheral = true;
    }

    @Override
    protected void peripheralUnblocked(@NotNull BlockedPeripheral<MockPeripheral> peripheral) {
        this.unblockedPeripheral = true;
    }

    @Override
    protected void peripheralAttached(@NotNull MockPeripheral peripheral) throws Exception {
        if (errorOnAttach) {
            throw new RuntimeException();
        }
        this.attachedPeripheral = true;
        super.peripheralAttached(peripheral);
    }

    @Override
    protected void peripheralAttachFailed(@NotNull MockPeripheral peripheral,
                                          @NotNull Throwable cause) {
        this.failedAttach = true;
    }

    @Override
    protected void peripheralDetached(@NotNull MockPeripheral peripheral) throws Exception {
        if (errorOnDetach) {
            throw new RuntimeException();
        }
        this.detachedPeripheral = true;
        super.peripheralDetached(peripheral);
    }

    @Override
    protected void peripheralDetachFailed(@NotNull MockPeripheral peripheral,
                                          @NotNull Throwable cause) {
        this.failedDetach = true;
    }

    @Override
    protected void peripheralConnected(@NotNull MockPeripheral peripheral) {
        this.connectedPeripheral = true;
    }

    @Override
    protected void peripheralDisconnected(@NotNull MockPeripheral peripheral) {
        this.disconnectedPeripheral = true;
    }

}
