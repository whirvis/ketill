package io.ketill.hidusb;

import io.ketill.KetillException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.usb4java.Device;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibUsbDeviceSeekerTest {

    @BeforeAll
    static void __init__() {
        /*
         * It would be strenuous on system resources to have a scan
         * interval less than or equal to zero milliseconds. It also
         * makes no sense to have a negative scan interval. As such,
         * assume this was a user error and thrown an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockLibUsbDeviceSeeker(0));
    }

    private LibUsbDevice usbDevice;
    private MockLibUsbDeviceSeeker seeker;

    @BeforeEach
    void setup() {
        this.usbDevice = mock(LibUsbDevice.class);

        Device underlyingDevice = mock(Device.class);
        when(usbDevice.getUsbDevice()).thenReturn(underlyingDevice);
        when(usbDevice.getVendorId()).thenReturn(0x0308);
        when(usbDevice.getProductId()).thenReturn(0x0511);

        this.seeker = new MockLibUsbDeviceSeeker();
    }

    @Test
    void getScanIntervalMs() {
        assertEquals(LibUsbDeviceSeeker.DEFAULT_SCAN_INTERVAL,
                seeker.getScanIntervalMs());
    }

    @Test
    void seekProduct() {
        /* use two products for full coverage */
        int vendorId_0 = 0x1234, productId_0 = 0x4567;
        int vendorId_1 = 0x89AB, productId_1 = 0xCDEF;
        assertFalse(seeker.isSeekingProduct(vendorId_0, productId_0));
        assertFalse(seeker.isSeekingProduct(vendorId_1, productId_1));

        seeker.seekProduct(vendorId_0, productId_0);
        seeker.seekProduct(vendorId_1, productId_1);
        assertTrue(seeker.isSeekingProduct(vendorId_0, productId_0));
        assertTrue(seeker.isSeekingProduct(vendorId_1, productId_1));
    }

    @Test
    void dropProduct() {
        /* connect device for next test */
        seeker.seekDeviceProduct(usbDevice);
        seeker.usbDeviceAttached(usbDevice);

        /*
         * If a device is dropped after it has connected, the seeker
         * must disconnect it automatically. It would not make sense
         * for a device that is no longer being sought for to linger
         * after being dropped.
         */
        seeker.disconnectedDevice = false;
        seeker.dropProduct(usbDevice.getVendorId(), usbDevice.getProductId());
        assertTrue(seeker.disconnectedDevice);

        assertFalse(seeker.isSeekingProduct(usbDevice.getVendorId(),
                usbDevice.getProductId()));
    }

    @Test
    void blacklistDevice() {
        /* connect device for next test */
        seeker.seekDeviceProduct(usbDevice);
        seeker.usbDeviceAttached(usbDevice);

        /*
         * Blacklisted devices which are still connected should be
         * immediately disconnected. It would not make sense for a
         * blacklisted device to linger afterward.
         */
        seeker.disconnectedDevice = false;
        seeker.blacklistDevice(usbDevice);
        assertTrue(seeker.disconnectedDevice);

        /*
         * In the event an attached USB device is blacklisted, it
         * should never be connected again. Devices are usually
         * blacklisted after they have caused some sort of issue,
         * usually by causing an exception to be thrown.
         */
        seeker.connectedDevice = false;
        seeker.usbDeviceAttached(usbDevice);
        assertFalse(seeker.connectedDevice);

        /*
         * In order to check that exemptDevice() is functioning
         * properly, a device must first be blacklisted. So, it
         * works out well to just test it here.
         */
        seeker.exemptDevice(usbDevice);

        /*
         * In the event a blacklisted device is exempted, that
         * being removed from the blacklist, it should be able
         * to connect once more. If a device cannot reconnect
         * after being exempted, a promise has been broken.
         */
        seeker.connectedDevice = false;
        seeker.usbDeviceAttached(usbDevice);
        assertTrue(seeker.connectedDevice);
    }

    @Test
    void usbDeviceAttached() {
        /*
         * If a USB device is not being sought after, the seeker
         * should not make a call to onAttach(). Furthermore, it
         * should make a call to unref() to ensure the device is
         * freed from memory.
         */
        seeker.connectedDevice = false;
        seeker.usbDeviceAttached(usbDevice);
        verify(usbDevice).unref();
        assertFalse(seeker.connectedDevice);

        /* begin seeking product for next tests */
        seeker.seekDeviceProduct(usbDevice);

        /*
         * If a USB device fails to open, the seeker should not
         * call onAttach(), even if it is currently being sought
         * after. It would make no sense to call onAttach() as
         * the device cannot be communicated with.
         */
        doThrow(new RuntimeException()).when(usbDevice).open();
        seeker.usbDeviceAttached(usbDevice);
        assertFalse(seeker.connectedDevice);

        /* exempt device for next test */
        seeker.exemptDevice(usbDevice);

        /*
         * Now that the device is being sought after, and the USB
         * device successfully opens, the seeker must make a call
         * to onAttach().
         */
        doNothing().when(usbDevice).open();
        seeker.usbDeviceAttached(usbDevice);
        assertTrue(seeker.connectedDevice);

        /*
         * Now that the USB device has successfully connected, the
         * seeker should not reconnect it unless it has previously
         * disconnected.
         */
        seeker.connectedDevice = false;
        seeker.usbDeviceAttached(usbDevice);
        assertFalse(seeker.connectedDevice);
    }

    @Test
    void usbDeviceDetached() {
        /*
         * If a device was not previously connected, it would not
         * make sense for it to be disconnected. As such, calling
         * onDisconnect() would not make sense.
         */
        seeker.disconnectedDevice = false;
        seeker.usbDeviceDetached(usbDevice);
        assertFalse(seeker.disconnectedDevice);

        /* attach device for next test */
        seeker.seekDeviceProduct(usbDevice);
        seeker.usbDeviceAttached(usbDevice);

        /* now onDisconnect() should be called */
        seeker.disconnectedDevice = false;
        seeker.usbDeviceDetached(usbDevice);
        assertTrue(seeker.disconnectedDevice);
    }

    @Test
    void scanDevices() {
        seeker.seekDeviceProduct(usbDevice);

        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            List<LibUsbDevice> connected = new ArrayList<>();
            libUsbDevice.when(() -> LibUsbDevice.getConnected(any(), any()))
                    .thenReturn(connected);

            /*
             * When the presence of a device is detected for the
             * first time in a scan, assuming it is being sought
             * for, a call to onDeviceConnect() must be made.
             */
            seeker.connectedDevice = false;
            connected.add(usbDevice);
            seeker.scanDevices();
            assertTrue(seeker.connectedDevice);

            /*
             * If the presence of a device is detected in a later
             * scan, onDeviceConnect() should not be called again
             * unless the device was previously disconnected.
             */
            seeker.connectedDevice = false;
            seeker.scanDevices();
            assertFalse(seeker.connectedDevice);

            /*
             * The first time a device is found to be absent in a
             * device scan, assuming it was previously connected,
             * onDeviceDisconnect() must be called.
             */
            seeker.disconnectedDevice = false;
            connected.remove(usbDevice);
            seeker.scanDevices();
            assertTrue(seeker.disconnectedDevice);

            /*
             * If the device is found to be absent in a later scan,
             * onDeviceDisconnect() should not be called again if
             * the device in question has not since reconnected.
             */
            seeker.disconnectedDevice = false;
            seeker.scanDevices();
            assertFalse(seeker.disconnectedDevice);
        }
    }

    @Test
    void onDeviceConnect() {
        seeker.seekDeviceProduct(usbDevice);

        /*
         * When an unchecked exception is thrown, attach() must
         * unreference the device and then throw the exception
         * back to the caller. This ensures a memory leak doesn't
         * follow if the program recovers.
         */
        seeker.errorOnDeviceConnect = true;
        assertThrows(RuntimeException.class,
                () -> seeker.usbDeviceAttached(usbDevice));
        verify(usbDevice).unref();
    }

    @Test
    void onDeviceDisconnect() {
        /* connect device for next test */
        seeker.seekDeviceProduct(usbDevice);
        seeker.usbDeviceAttached(usbDevice);

        /*
         * When an unchecked exception is thrown, detach() must
         * unreference the device and then throw the exception
         * back to the caller. This ensures a memory leak doesn't
         * follow if the program recovers.
         */
        seeker.errorOnDeviceDisconnect = true;
        assertThrows(RuntimeException.class,
                () -> seeker.usbDeviceDetached(usbDevice));
        verify(usbDevice).unref();
    }

    @Test
    void seekImpl() throws InterruptedException {
        /*
         * It makes no sense to scan for devices when no products
         * have been specified. As such, assume this was a mistake
         * by the user and thrown an exception.
         */
        assertThrows(KetillException.class, () -> seeker.seek());

        /* specify product to seek */
        seeker.seekProduct(0x0000, 0x0000);
        assertDoesNotThrow(seeker::seek);

        /*
         * To save processing power, LibUSB device seekers have a
         * scan interval specified during construction. This way,
         * if seek() is called many times in a short period, the
         * seeker won't rapidly make expensive calls.
         */
        Thread.sleep(seeker.getScanIntervalMs() / 10);
        seeker.seek(); /* not enough time has passed */
        assertEquals(1, seeker.deviceScanCount);

        /*
         * However, after enough time has passed, the LibUSB device
         * seeker is expected to perform another scan. If this does
         * not occur, something has gone wrong.
         */
        Thread.sleep(seeker.getScanIntervalMs());
        seeker.seek(); /* enough time has passed now */
        assertEquals(2, seeker.deviceScanCount);
    }

    @Test
    void close() {
        /* connect device for next test */
        seeker.seekDeviceProduct(usbDevice);
        seeker.usbDeviceAttached(usbDevice);

        /*
         * When a LibUSB device seeker is closed, it is expected to
         * close all connected LibUSB devices to prevent a possible
         * memory leak from occurring. Afterwards, it must exit its
         * current LibUSB context.
         */
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            seeker.close();
            verify(usbDevice).close();
            libUsbDevice.verify(() -> LibUsbDevice.exitContext(any()));
        }
    }

    @AfterEach
    void shutdown() {
        seeker.close();
    }

}