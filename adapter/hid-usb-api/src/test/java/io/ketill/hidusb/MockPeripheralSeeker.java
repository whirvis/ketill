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
    boolean setupPeripheral, shutdownPeripheral;
    boolean blockOnSetup, blockOnShutdown;
    boolean errorOnSetup, errorOnShutdown;
    boolean failedSetup, failedShutdown;
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

    void reset() {
        this.targetedProduct = false;
        this.droppedProduct = false;
        this.blockedPeripheral = false;
        this.unblockedPeripheral = false;
        this.setupPeripheral = false;
        this.shutdownPeripheral = false;
        this.blockOnSetup = false;
        this.blockOnShutdown = false;
        this.errorOnSetup = false;
        this.errorOnShutdown = false;
        this.failedSetup = false;
        this.failedShutdown = false;
        this.connectedPeripheral = false;
        this.disconnectedPeripheral = false;
    }

    void attachMock(@NotNull MockPeripheral peripheral) {
        requireScanWaitDisabled();
        if (!attached.contains(peripheral)) {
            attached.add(peripheral);
        }
        this.seek(); /* scan for peripherals */
    }

    void detachMock(@NotNull MockPeripheral peripheral) {
        requireScanWaitDisabled();
        attached.remove(peripheral);
        this.seek(); /* scan for peripherals */
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
    protected void setupPeripheral(@NotNull MockPeripheral peripheral) {
        if (blockOnSetup) {
            this.blockPeripheral(peripheral, true);
        }
        if (errorOnSetup) {
            throw new RuntimeException();
        }
        this.setupPeripheral = true;
    }

    @Override
    protected void peripheralSetupFailed(@NotNull MockPeripheral peripheral,
                                         @NotNull Throwable cause) {
        super.peripheralSetupFailed(peripheral, cause);
        this.failedSetup = true;
    }

    @Override
    protected void shutdownPeripheral(@NotNull MockPeripheral peripheral) {
        if (blockOnShutdown) {
            this.blockPeripheral(peripheral, true);
        }
        if (errorOnShutdown) {
            throw new RuntimeException();
        }
        this.shutdownPeripheral = true;
    }


    @Override
    protected void peripheralShutdownFailed(@NotNull MockPeripheral peripheral,
                                            @NotNull Throwable cause) {
        super.peripheralShutdownFailed(peripheral, cause);
        this.failedShutdown = true;
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
