package io.ketill.hidusb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.usb4java.Context;
import org.usb4java.LibUsbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class LibUsbDeviceSeekerTest {

    private ProductId productId;
    private LibUsbDevice peripheral;
    private MockLibUsbDeviceSeeker seeker;

    @BeforeEach
    void createSeeker() {
        LibUsbDeviceSeeker.scanWaitDisabled = true;

        this.productId = new ProductId(0x1234, 0x5678);
        this.peripheral = mock(LibUsbDevice.class);
        when(peripheral.getProductId()).thenReturn(productId);

        this.seeker = new MockLibUsbDeviceSeeker(LibUsbDevice::new);
        seeker.targetProduct(productId);
    }

    @Test
    void testInit() {
        long scanIntervalMs = LibUsbDeviceSeeker.MINIMUM_SCAN_INTERVAL;
        Context context = mock(Context.class);

        /*
         * In LibUSB, NULL signifies the default context. Using the default
         * context in the LibUSB device seeker is forbidden. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(scanIntervalMs, null,
                        LibUsbDevice::new));
        assertThrows(NullPointerException.class,
                () -> new MockLibUsbDeviceSeeker(null, LibUsbDevice::new));

        /*
         * The device supplier is used by the device seeker to instantiate
         * LibUsbDevice instances when scanning the system. Having a null
         * device supplier would make this impossible.
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
             * The context returned by LibUsbDevice.initContext() is a mock
             * here. As such, these seekers do not need to be closed (there
             * are no resources to free.)
             */
            new MockLibUsbDeviceSeeker(scanIntervalMs, LibUsbDevice::new);
            new MockLibUsbDeviceSeeker(LibUsbDevice::new);

            /*
             * The two constructors above do not take a parameter for the
             * LibUSB context. They are both expected to initialize their
             * own context and use that instead.
             */
            libUsbDevice.verify(LibUsbDevice::initContext, times(2));
        }
    }

    @Test
    void testGetId() {
        assertEquals(productId, seeker.getId(peripheral));
    }

    @Test
    void testGetHash() {
        assertEquals(peripheral.hashCode(), seeker.getHash(peripheral));
    }

    @Test
    void testScanPeripherals() {
        /*
         * The LibUSB device seeker should get currently connected devices
         * using the LibUsbDevice class. This method is verified to work in
         * the LibUsbDeviceTest class.
         */
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            seeker.seek(); /* trigger peripheral scan */
            libUsbDevice.verify(() -> LibUsbDevice.getConnected(any(), any()));
        }
    }

    @Test
    void testSetupPeripheral() {
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
             * When a LibUSB device is attached and setup, the LibUSB device
             * seeker should open a handle before connecting the peripheral
             * If this fails, corrective measures can be taken before the
             * peripheral is finally connected.
             */
            seeker.seek(); /* trigger peripheral setup */
            verify(peripheral).openHandle();
            assertTrue(seeker.connectedPeripheral);
        }
    }

    @Test
    void testSetupPeripheralFailed() {
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
             * If a non LibUSB error occurs while attempting to open the
             * handle for a LibUSB device, the device seeker should call
             * the super. This should cause the peripheral to be blocked,
             * as that is the default behavior for setup failure.
             */
            doThrow(RuntimeException.class).when(peripheral).openHandle();
            seeker.seek(); /* trigger failed setup */
            assertFalse(seeker.connectedPeripheral);
            assertTrue(seeker.isPeripheralBlocked(peripheral));

            /* unblock peripheral for next test */
            seeker.unblockPeripheral(peripheral);

            AtomicBoolean unblockAfterDetach = new AtomicBoolean();
            seeker.onBlockPeripheral((d, b) ->
                    unblockAfterDetach.set(b.unblockAfterDetach));

            /*
             * If a LibUSB error occurs while attempting to open the handle
             * for a LibUSB device, the device seeker should decrement the
             * remaining attempts for connection. When no attempts remain,
             * the device should be blocked until it is detached.
             */
            doThrow(LibUsbException.class).when(peripheral).openHandle();
            int attempts = seeker.getSetupAttempts(peripheral);
            for (int i = 0; i < attempts; i++) {
                seeker.seek(); /* trigger failed setup */
                boolean blocked = seeker.isPeripheralBlocked(peripheral);
                assertEquals(i + 1 >= attempts, blocked);
                if (blocked) {
                    assertTrue(unblockAfterDetach.get());
                }
            }
            assertFalse(seeker.connectedPeripheral);
        }
    }

    @Test
    void testShutdownPeripheral() {
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
             * When a LibUSB device is disconnected and shutdown, the device
             * seeker is responsible for closing the device. This is done to
             * prevent memory leaks.
             */
            connected.remove(peripheral);
            seeker.seek(); /* trigger detach */
            verify(peripheral).close();
            assertTrue(seeker.disconnectedPeripheral);
        }
    }

    @Test
    void testClose() {
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            /*
             * When a LibUsbDeviceSeeker is closed with a context that it
             * generated, it is expected to automatically exit it. This is
             * to free up resources the user is not in control of.
             */
            seeker.close(); /* trigger exiting of context */
            libUsbDevice.verify(() -> LibUsbDevice.exitContext(any()),
                    times(1));

            /* provide context for next test */
            Context context = mock(Context.class);
            MockLibUsbDeviceSeeker providedContext =
                    new MockLibUsbDeviceSeeker(context, LibUsbDevice::new);

            /*
             * However, if a LibUsbDeviceSeeker is closed with a context that
             * the user provided, do not close it! The user is the owner, and
             * is therefore responsible for it. Furthermore, they may desire
             * to continue using that context.
             */
            providedContext.close();
            libUsbDevice.verify(() -> LibUsbDevice.exitContext(any()),
                    times(1));
        }
    }

}
