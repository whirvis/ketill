package io.ketill.hidusb;

import io.ketill.KetillException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void dropProduct() {
    }

    @Test
    void blacklistDevice() {
    }

    @Test
    void seekImpl() {
        /*
         * It makes no sense to scan for devices when no products
         * have been specified. As such, assume this was a mistake
         * by the user and thrown an exception.
         */
        assertThrows(KetillException.class, () -> seeker.seek());

        /* specify product to seek */
        seeker.seekProduct(0x00, 0x00);
        assertDoesNotThrow(seeker::seek);
    }

    @Test
    void close() {
    }

    @AfterEach
    void shutdown() {
        seeker.close();
    }

}