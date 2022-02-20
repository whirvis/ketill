package io.ketill.hidusb;

import io.ketill.KetillException;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.event.HidServicesEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static io.ketill.hidusb.AssertUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HidDeviceSeekerTest {

    private HidDevice hidDevice;
    private HidServicesEvent hidEvent;
    private HidServices hidServices;
    private MockHidDeviceSeeker seeker;

    @BeforeAll
    static void __init__() {
        /*
         * It would be strenuous on system resources to have a scan
         * interval less than or equal to zero milliseconds. It also
         * makes no sense to have a negative scan interval. As such,
         * assume this was a user error and thrown an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockHidDeviceSeeker(0));
    }

    @BeforeEach
    void setup() {
        this.hidDevice = mock(HidDevice.class);
        when(hidDevice.getVendorId()).thenReturn(0x0308);
        when(hidDevice.getProductId()).thenReturn(0x0511);

        this.hidEvent = mock(HidServicesEvent.class);
        when(hidEvent.getHidDevice()).thenReturn(hidDevice);

        this.hidServices = mock(HidServices.class);

        /* @formatter:off */
        try (MockedStatic<HidManager> hidManager =
                     mockStatic(HidManager.class)) {
            hidManager.when(() -> HidManager.getHidServices(any()))
                    .thenReturn(hidServices);
            this.seeker = new MockHidDeviceSeeker();
        }
        /* @formatter:on */
    }

    @Test
    void seekProduct() {
        /* use two products for full coverage */
        int vendorId_0 = 0x1234, vendorId_1 = 0x89AB;
        int productId_0 = 0x4567, productId_1 = 0xCDEF;
        assertFalse(seeker.isSeekingProduct(vendorId_0, productId_0));
        assertFalse(seeker.isSeekingProduct(vendorId_1, productId_1));

        seeker.seekProduct(vendorId_0, productId_0);
        seeker.seekProduct(vendorId_1, productId_1);
        assertTrue(seeker.isSeekingProduct(vendorId_0, productId_0));
        assertTrue(seeker.isSeekingProduct(vendorId_1, productId_1));
    }

    @Test
    void blacklistDevice() {
        /* connect device for next test */
        seeker.seekProduct(hidDevice.getVendorId(), hidDevice.getProductId());
        when(hidDevice.open()).thenReturn(true);
        seeker.hidDeviceAttached(hidEvent);

        /*
         * Blacklisted devices which are still connected should be
         * immediately disconnected. It would not make sense for a
         * blacklisted device to linger afterward.
         */
        seeker.disconnectedDevice = false;
        seeker.blacklistDevice(hidDevice);
        assertTrue(seeker.disconnectedDevice);

        /*
         * In the event a blacklisted HID device is attached, it
         * should never be connected again. Devices are usually
         * blacklisted after they have caused some sort of issue,
         * usually by causing an exception to be thrown.
         */
        seeker.connectedDevice = false;
        seeker.hidDeviceAttached(hidEvent);
        assertFalse(seeker.connectedDevice);
    }

    @Test
    void dropProduct() {
        /* connect device for next test */
        seeker.seekProduct(hidDevice.getVendorId(), hidDevice.getProductId());
        when(hidDevice.open()).thenReturn(true);
        seeker.hidDeviceAttached(hidEvent);

        /*
         * If a device is dropped after it has connected, the seeker
         * must disconnect it automatically. It would not make sense
         * for a device that is no longer being sought for to linger
         * after being dropped.
         */
        seeker.disconnectedDevice = false;
        seeker.dropProduct(hidDevice.getVendorId(), hidDevice.getProductId());
        assertTrue(seeker.disconnectedDevice);

        assertFalse(seeker.isSeekingProduct(hidDevice.getVendorId(),
                hidDevice.getProductId()));
    }

    @Test
    void hidDeviceAttached() {
        /*
         * If an HID device is not being sought after, the seeker
         * should not make a call to onConnect() or even attempt
         * to open it. It would make no sense to open the device
         * if it's not going to be passed onto onConnect().
         */
        seeker.connectedDevice = false;
        seeker.hidDeviceAttached(hidEvent);
        verify(hidDevice, never()).open();
        assertFalse(seeker.connectedDevice);

        /* begin seeking product for next tests */
        seeker.seekProduct(hidDevice.getVendorId(), hidDevice.getProductId());

        /*
         * If an HID device fails to open, the seeker should not
         * call onConnect(), even if it is currently being sought
         * after. It would make no sense to call onConnect() as
         * the device cannot be communicated with.
         */
        when(hidDevice.open()).thenReturn(false);
        seeker.hidDeviceAttached(hidEvent);
        assertFalse(seeker.connectedDevice);

        /*
         * Now that the device is being sought after, and the HID
         * device successfully opens, the seeker must make a call
         * to onConnect().
         */
        when(hidDevice.open()).thenReturn(true);
        seeker.hidDeviceAttached(hidEvent);
        assertTrue(seeker.connectedDevice);

        /*
         * Now that the HID device has successfully connected, the
         * seeker should not reconnect it unless it has previously
         * disconnected.
         */
        seeker.connectedDevice = false;
        seeker.hidDeviceAttached(hidEvent);
        assertFalse(seeker.connectedDevice);

        /*
         * For the next test, we want the HID exception caught in
         * hidDeviceAttached() to be thrown. If no devices are being
         * sought, an IllegalStateException will be thrown instead.
         */
        seeker.seekProduct(0x00, 0x00);

        /*
         * When an exception is thrown, hidDeviceAttached() must
         * catch it and pass it onto seekImpl() for the next call
         * to seek(). This ensures it doesn't fall under the radar.
         *
         * HidServicesEvent.getHidDevice() is used specifically
         * here (rather than HidDevice.open()) as the device has
         * already been connected. As such, the seeker will not
         * open it again.
         */
        RuntimeException hidException = new RuntimeException();
        when(hidEvent.getHidDevice()).thenThrow(hidException);
        seeker.hidDeviceAttached(hidEvent);
        assertThrowsCause(hidException, seeker::seek);
    }

    @Test
    void hidDeviceDetached() {
        /*
         * If a device was not previously connected, it would not
         * make sense for it to be disconnected. As such, calling
         * onDisconnect() would not make sense.
         */
        seeker.disconnectedDevice = false;
        seeker.hidDeviceDetached(hidEvent);
        assertFalse(seeker.disconnectedDevice);

        /* attach device for next test */
        seeker.seekProduct(hidDevice.getVendorId(), hidDevice.getProductId());
        when(hidDevice.open()).thenReturn(true);
        seeker.hidDeviceAttached(hidEvent);

        /* now onDisconnect() should be called */
        seeker.disconnectedDevice = false;
        seeker.hidDeviceDetached(hidEvent);
        assertTrue(seeker.disconnectedDevice);

        /*
         * For the next test, we want the HID exception caught in
         * hidDeviceDetached() to be thrown. If no devices are being
         * sought, an IllegalStateException will be thrown instead.
         */
        seeker.seekProduct(0x00, 0x00);

        /*
         * When an exception is thrown, hidDeviceDetached() must
         * catch it and pass it onto seekImpl() for the next call
         * to seek(). This ensures it doesn't fall under the radar.
         */
        RuntimeException hidException = new RuntimeException();
        when(hidEvent.getHidDevice()).thenThrow(hidException);
        seeker.hidDeviceDetached(hidEvent);
        assertThrowsCause(hidException, seeker::seek);
    }

    @Test
    void hidFailure() {
        /*
         * If an HID device fails, it should be blacklisted. As a
         * result, if the same device is attached later, it should
         * not be connected.
         */
        seeker.connectedDevice = false;
        seeker.hidFailure(hidEvent);
        seeker.hidDeviceAttached(hidEvent);
        assertFalse(seeker.connectedDevice);

        /*
         * For the next test, we want the HID exception caught in
         * hidFailure() to be thrown. If no devices are being sought
         * after, an IllegalStateException will be thrown instead.
         */
        seeker.seekProduct(0x00, 0x00);

        /*
         * When an exception is thrown, hidFailure() must catch it
         * and pass it onto seekImpl() for the next call to seek()
         * This ensures it doesn't fall under the radar.
         */
        RuntimeException hidException = new RuntimeException();
        when(hidEvent.getHidDevice()).thenThrow(hidException);
        seeker.hidFailure(hidEvent);
        assertThrowsCause(hidException, seeker::seek);
    }

    @Test
    void seekImpl() {
        /*
         * It makes no sense to scan for devices when no products
         * have been specified. As such, assume this was a mistake
         * by the user and thrown an exception.
         */
        assertThrows(KetillException.class, () -> seeker.seek());
        seeker.seekProduct(0x00, 0x00);

        /*
         * The first time the HID device seeker performs a device
         * scan, it should start the HID services created during
         * instantiation. This is required for it to be notified
         * when an HID device is attached or detached.
         */
        seeker.seek();
        verify(hidServices).start();
    }

    @Test
    void close() {
        /* connect device for next test */
        seeker.seekProduct(hidDevice.getVendorId(), hidDevice.getProductId());
        when(hidDevice.open()).thenReturn(true);
        seeker.hidDeviceAttached(hidEvent);

        /*
         * When an HID device seeker is closed, it is expected to
         * close all connected HID devices to prevent a possible
         * memory leak from occurring. Afterwards, it must shut
         * down the HID services it instantiated at startup.
         */
        seeker.close();
        verify(hidDevice).close();
        verify(hidServices).stop();
    }

    @AfterEach
    void shutdown() {
        seeker.close();
    }

}