package io.ketill.hidusb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.usb4java.Context;
import org.usb4java.LibUsbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class LibUsbDeviceSeekerTest {

    @Test
    void __init__() {
        long scanIntervalMs = LibUsbDeviceSeeker.MINIMUM_SCAN_INTERVAL;
        Context context = mock(Context.class);

        /*
         * In LibUSB, NULL signifies the default context.
         * Using the default context in the LibUSB device
         * seeker is forbidden. As such, assume this was
         * a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(scanIntervalMs, null,
                        LibUsbDevice::new));
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(null, LibUsbDevice::new));

        /*
         * The device supplier is used by the libUSB device
         * seeker to instantiate LibUsbDevice instances when
         * scanning the system. Using a null device supplier
         * would make this impossible. As such, assume this
         * was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(scanIntervalMs, context,
                        null));
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(scanIntervalMs, null));
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(context, null));
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(null));

        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            libUsbDevice.when(LibUsbDevice::initContext).thenReturn(context);

            /*
             * The context returned by LibUsbDevice.initContext()
             * is a mock here. As such, these seekers don't need
             * to be closed (there's no resources to free).
             */
            new MockLibUsbDeviceSeeker(scanIntervalMs, LibUsbDevice::new);
            new MockLibUsbDeviceSeeker(LibUsbDevice::new);

            /*
             * The two constructors above do not take a parameter
             * for the LibUSB context. They are both expected to
             * initialize their own context and use that instead.
             */
            libUsbDevice.verify(LibUsbDevice::initContext, times(2));
        }
    }

    private ProductId productId;
    private LibUsbDevice peripheral;
    private MockLibUsbDeviceSeeker seeker;

    @BeforeEach
    void setup() {
        LibUsbDeviceSeeker.scanWaitDisabled = true;

        this.productId = new ProductId(0x1234, 0x5678);
        this.peripheral = mock(LibUsbDevice.class);
        when(peripheral.getProductId()).thenReturn(productId);

        this.seeker = new MockLibUsbDeviceSeeker(LibUsbDevice::new);
        seeker.targetProduct(productId);
    }

    @Test
    void minimumScanInterval() {
        /*
         * Since the mock LibUsbDeviceSeeker uses the super
         * constructor, it should be using the default scan
         * interval, which is the minimum value allowed.
         */
        assertEquals(LibUsbDeviceSeeker.MINIMUM_SCAN_INTERVAL,
                seeker.scanIntervalMs);
    }

    @Test
    void defaultSetupAttempts() {
        /*
         * Since the mock LibUsbDeviceSeeker does not override
         * the getSetupAttempts() method, it should return the
         * default value.
         */
        assertEquals(LibUsbDeviceSeeker.DEFAULT_SETUP_ATTEMPTS,
                seeker.getSetupAttempts(peripheral));
    }

    @Test
    void getId() {
        ProductId id = seeker.getId(peripheral);
        assertEquals(productId, id);
    }

    @Test
    void getHash() {
        int hash = seeker.getHash(peripheral);
        assertEquals(peripheral.hashCode(), hash);
    }

    @Test
    void scanPeripherals() {
        /*
         * The LibUSB device seeker should get the currently
         * connected devices using the LibUsbDevice class.
         * It is verified to work via the LibUsbDeviceTest.
         */
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            seeker.seek(); /* trigger peripheral scan */
            libUsbDevice.verify(() -> LibUsbDevice.getConnected(any(), any()));
        }
    }

    @Test
    void setupPeripheral() {
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            /* mock connection for next test */
            /* @formatter:off */
            List<LibUsbDevice> connected =
                    Collections.singletonList(peripheral);
            libUsbDevice.when(() -> LibUsbDevice.getConnected(
                    any(), any())).thenReturn(connected);
            /* @formatter:on */

            /*
             * When a LibUSB device is attached and setup, the
             * LibUSB device seeker is expected to open a handle
             * before connecting the peripheral. If this fails,
             * the corrective measures can be taken before the
             * peripheral is finally connected.
             */
            seeker.seek(); /* trigger peripheral setup */
            verify(peripheral).openHandle();
            assertTrue(seeker.connectedPeripheral);
        }
    }

    @Test
    void setupPeripheralFailed() {
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            /* mock connection for next test */
            /* @formatter:off */
            List<LibUsbDevice> connected =
                    Collections.singletonList(peripheral);
            libUsbDevice.when(() -> LibUsbDevice.getConnected(
                    any(), any())).thenReturn(connected);
            /* @formatter:on */

            /*
             * If a non LibUSB error occurs while attempting to
             * open the handle for a LibUSB device, the LibUSB
             * device seeker should call the super. This should
             * result in the peripheral being blocked, as that
             * is the default behavior for setup failure.
             */
            doThrow(RuntimeException.class).when(peripheral).openHandle();
            seeker.seek(); /* trigger failed setup */
            assertFalse(seeker.connectedPeripheral);
            assertTrue(seeker.isPeripheralBlocked(peripheral));

            /* unblock peripheral for next test */
            seeker.unblockPeripheral(peripheral);

            /*
             * When a LibUSB error occurs while attempting to
             * open the handle for a device, the device seeker
             * should decrement the remaining attempts for the
             * device to connect. When no more attempts remain,
             * the LibUSB device should be blocked.
             */
            doThrow(LibUsbException.class).when(peripheral).openHandle();
            int attempts = seeker.getSetupAttempts(peripheral);
            for (int i = 0; i < attempts; i++) {
                seeker.seek(); /* trigger failed setup */
                boolean blocked = seeker.isPeripheralBlocked(peripheral);
                assertEquals(i + 1 >= attempts, blocked);
            }
            assertFalse(seeker.connectedPeripheral);
        }
    }

    @Test
    void shutdownPeripheral() {
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            /* mock connection for next test */
            /* @formatter:off */
            List<LibUsbDevice> connected = new ArrayList<>();
            connected.add(peripheral);
            libUsbDevice.when(() -> LibUsbDevice.getConnected(
                    any(), any())).thenReturn(connected);
            /* @formatter:on */

            /* attach peripheral for next test */
            seeker.seek(); /* trigger attach */

            /*
             * When a device is disconnected and shutdown, the
             * LibUSB device seeker is considered responsible
             * for closing the LibUSB device. This is done to
             * prevent memory leaks.
             */
            connected.remove(peripheral);
            seeker.seek(); /* trigger detach */
            verify(peripheral).close();
            assertTrue(seeker.disconnectedPeripheral);
        }
    }

    @Test
    void close() {
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            /*
             * When a LibUsbDeviceSeeker is closed with a context
             * that it generated, it is expected to automatically
             * exit it. This is to free up resources that the user
             * does not have access to.
             */
            seeker.close(); /* trigger exiting of context */
            libUsbDevice.verify(() -> LibUsbDevice.exitContext(any()),
                    times(1));

            /* provide context for next test */
            Context context = mock(Context.class);
            MockLibUsbDeviceSeeker providedContext =
                    new MockLibUsbDeviceSeeker(context, LibUsbDevice::new);

            /*
             * However, if a LibUsbDeviceSeeker is closed with a
             * context that the user provided, do not close it!
             * The user is the one who owns it (and is therefore
             * responsible for it). Furthermore, they may desire
             * to continue using that context.
             */
            providedContext.close();
            libUsbDevice.verify(() -> LibUsbDevice.exitContext(any()),
                    times(1));
        }
    }

}
