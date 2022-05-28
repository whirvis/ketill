package io.ketill.hidusb;

import io.ketill.ToStringVerifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.opentest4j.TestAbortedException;
import org.usb4java.Context;
import org.usb4java.DescriptorUtils;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class LibUsbDeviceTest {

    @Test
    void testRequireSuccess() {
        /*
         * It would not make sense to require a null operation to succeed.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.requireSuccess(null));

        /*
         * Any value less than zero will result in a LibUsbException being
         * thrown by the LibUSB library. This exception must be thrown back
         * to the caller.
         */
        assertThrows(LibUsbException.class,
                () -> LibUsbDevice.requireSuccess(() -> LibUsb.ERROR_IO));

        /*
         * When a LibUSB operation is successful, its return code should be
         * returned to the caller. Not doing so could make this unusable in
         * certain situations. The generated value is given a positive bound
         * to ensure a negative value is never returned.
         */
        int success = new Random().nextInt(1024);
        AtomicInteger result = new AtomicInteger();
        assertDoesNotThrow(() -> {
            int code = LibUsbDevice.requireSuccess(() -> success);
            result.set(code);
        });
        assertEquals(success, result.get());
    }

    @Test
    void testGetZadigHomepage() {
        /*
         * While specified as a possibility in documentation, this method
         * should never return null. If it does, something has gone wrong.
         * The null return value is to prevent an exception.
         */
        assertNotNull(LibUsbDevice.getZadigHomepage());
    }

    @Test
    void testOpenZadigHomepage() throws IOException {
        try (MockedStatic<Desktop> awtDesktop = mockStatic(Desktop.class)) {
            /*
             * If Java AWT desktop is not supported, then opening the home
             * page for Zadig will not succeed. As such, this method should
             * return false.
             */
            awtDesktop.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(LibUsbDevice.openZadigHomepage());
            awtDesktop.when(Desktop::isDesktopSupported).thenReturn(true);

            Desktop desktop = mock(Desktop.class);
            awtDesktop.when(Desktop::getDesktop).thenReturn(desktop);

            /*
             * If the browse action is not supported, then opening the home
             * page for Zadig will not succeed. As such, this method should
             * return false.
             */
            when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(false);
            assertFalse(LibUsbDevice.openZadigHomepage());
            when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

            /*
             * If calling browse() causes an I/O exception, the home page
             * was not opened. As such, this method should return false.
             */
            doThrow(new IOException()).when(desktop).browse(any());
            assertFalse(LibUsbDevice.openZadigHomepage());
            doNothing().when(desktop).browse(any());

            /*
             * If everything goes right when opening the home page, this
             * method should return true. It should have also made a call
             * to the browse() method.
             */
            assertTrue(LibUsbDevice.openZadigHomepage());
            verify(desktop, times(2)).browse(any());
        }

        /* @formatter:off */
        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            libUsbDevice.when(LibUsbDevice::getZadigHomepage)
                    .thenReturn(null);

            /*
             * If the URI for the Zadig home page could not be resolved, it
             * will not be possible to open it. As such, this method should
             * return false.
             */
            libUsbDevice.when(LibUsbDevice::openZadigHomepage)
                    .thenCallRealMethod();
            assertFalse(LibUsbDevice.openZadigHomepage());
        }
        /* @formatter:on */
    }

    private static Context context;

    private static void assumeContextPresent() {
        assumeTrue(context != null, "no LibUSB context");
    }

    private static void libUsbError(LibUsbException cause) {
        throw new TestAbortedException("LibUSB error", cause);
    }

    @BeforeAll
    static void initContext() {
        try {
            context = LibUsbDevice.initContext();
            assertNotNull(context);
        } catch (LibUsbException e) {
            /*
             * This LibUSB error can be ignored. Not all tests require a
             * valid context. Test which do will check if one is present
             * before proceeding.
             */
        }
    }

    @Test
    void testGetConnected() {
        /*
         * Using the default context (which is represented as null in LibUSB)
         * is forbidden. Furthermore, a LibUSB device supplier is required to
         * instantiate wrappers for scanned devices. If either of these are
         * null, assume it was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.getConnected(null, LibUsbDevice::new));
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.getConnected(context, null));

        assumeContextPresent();

        try {
            List<LibUsbDevice> connected = LibUsbDevice.getConnected(context,
                    LibUsbDevice::new);

            /*
             * If retrieving connected devices succeeds, the returned list
             * should never be null (however, it can be empty). If the list
             * is not empty, none of its elements should be null either.
             */
            assertNotNull(connected);
            for (LibUsbDevice device : connected) {
                assertNotNull(device);
            }

            /*
             * This next test can only be run if at least one USB device is
             * connected. If none are connected, the nullability check will
             * never be reached; resulting in a failing assertion.
             */
            if (!connected.isEmpty()) {
                /*
                 * It would not make sense for LibUSB device supplier to
                 * return a null value. Assume this was a mistake by the
                 * user and throw an exception.
                 */
                /* @formatter:off */
                assertThrows(NullPointerException.class,
                        () -> LibUsbDevice.getConnected(context,
                                (c, d) -> null));
                /* @formatter:on */
            }
        } catch (LibUsbException e) {
            libUsbError(e);
        }
    }

    @Test
    void testCloseDevices() {
        /*
         * It would not make sense to pass a null iterable of devices to
         * close, nor have a null device as one of its elements. As such,
         * assume this was a mistake by the user and throw an exception.
         */
        List<LibUsbDevice> nullList = Collections.singletonList(null);
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.closeDevices(null));
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.closeDevices(nullList));

        /* prepare list of devices for next test */
        List<LibUsbDevice> devices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            devices.add(mock(LibUsbDevice.class));
        }

        /*
         * As the name of this method implies, it should close all devices it
         * is given (even if they are already closed). Failure to do so could
         * result in a memory leak.
         */
        LibUsbDevice.closeDevices(devices);
        for (LibUsbDevice device : devices) {
            verify(device).close();
        }
    }

    @Test
    void testGetClassName() {
        /*
         * Since the name of the USB class is known, this method should
         * return exactly what is returned by the DescriptorUtils method.
         */
        assertEquals(DescriptorUtils.getUSBClassName(LibUsb.CLASS_HID),
                LibUsbDevice.getClassName(LibUsb.CLASS_HID, false));

        /*
         * Since the USB class here is unknown. As such, this method should
         * return the USB class as a hexadecimal string when requested. If
         * not requested, a value of null should be returned.
         */
        byte manufacturer = (byte) 0x38;
        assertEquals(String.format("0x%02x", manufacturer),
                LibUsbDevice.getClassName(manufacturer, true));
        assertNull(LibUsbDevice.getClassName(manufacturer));
    }

    private LibUsbDevice guineaPig;

    private void assumeGuineaPigPresent() {
        assumeTrue(guineaPig != null, "no LibUSB device");
    }

    @BeforeEach
    void findGuineaPig() {
        /*
         * Do not use assumeContextPresent() here. There are some tests which
         * do not rely on a context at all. Requiring for it to be present in
         * setup would mean they do not run even though it is not required.
         */
        if (context == null) {
            return;
        }

        List<LibUsbDevice> connected = null;
        try {
            connected = LibUsbDevice.getConnected(context, LibUsbDevice::new);
        } catch (LibUsbException e) {
            /* LibUSB error can be ignored, see above */
        }

        /*
         * Because LibUSB is more-or-less impossible to mock, the next best
         * solution is to run unit tests with a handle to a real USB device.
         * device. All tests that rely on a guinea pig device will simply be
         * skipped if one is not present.
         */
        if (connected != null && !connected.isEmpty()) {
            this.guineaPig = connected.get(0);
        }
    }

    @Test
    void testGetProductId() {
        assumeGuineaPigPresent();

        /*
         * If the underlying product ID is null, it means the type of device
         * is unknown. This would make it impossible for a LibUsbDeviceSeeker
         * to know what type of device this is.
         */
        assertNotNull(guineaPig.getProductId());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void testGetHandle() {
        assumeGuineaPigPresent();

        /*
         * If the USB handle has not yet been opened, assume the user has
         * made a mistake and throw an exception. The user is expected to
         * open the USB handle themselves beforehand.
         */
        assertThrows(IllegalStateException.class, () -> guineaPig.getHandle());
    }

    @Test
    void testOpenHandle() {
        assumeGuineaPigPresent();

        try {
            guineaPig.openHandle();
            assertNotNull(guineaPig.getHandle());
        } catch (LibUsbException e) {
            libUsbError(e);
        }
    }

    @Test
    void testGetString() {
        assumeGuineaPigPresent();

        /*
         * Since the handle has not been opened, it will not be possible to
         * get any strings descriptors from the device. As such, this method
         * should return the index as a hexadecimal string when requested.
         * Otherwise, a value of null should be returned.
         */
        byte manufacturer = (byte) 0x38;
        assertEquals(String.format("0x%02x", manufacturer),
                guineaPig.getString(manufacturer, true));
        assertNull(guineaPig.getString(manufacturer));
    }

    @Test
    void testClose() {
        assumeGuineaPigPresent();

        assertFalse(guineaPig.isClosed());
        guineaPig.close();
        assertTrue(guineaPig.isClosed());

        /*
         * When a LibUSB device wrapper is closed via close(), it is required
         * to unreference its underlying device (destroying it as a result).
         * Attempting to reference the underlying device after will confirm
         * if it was destroyed or not.
         */
        assertThrows(IllegalStateException.class,
                () -> LibUsb.refDevice(guineaPig.usbDevice));

        /*
         * It would not make sense to open a USB handle after this device
         * has been closed. As such, assume this was a user mistake.
         */
        assertThrows(IllegalStateException.class, guineaPig::openHandle);

        /*
         * It is legal to call close() on a LibUSB device after originally
         * closing it. This is to fall in line with the Closeable interface
         * as provided by Java.
         */
        assertDoesNotThrow(guineaPig::close);
    }

    @Test
    @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself"})
    void verifyEquals() {
        assumeGuineaPigPresent();

        /*
         * Originally, this test was going to utilize the EqualsVerifier
         * class. However, it was found to cause the JVM to crash due to
         * some error being thrown by the native libraries of LibUSB.
         */

        assertFalse(guineaPig.equals(null));
        assertFalse(guineaPig.equals(new Object()));
        assertFalse(guineaPig.equals(mock(LibUsbDevice.class)));

        assertTrue(guineaPig.equals(guineaPig));

        long ptr = guineaPig.usbDevice.getPointer();
        assertEquals(Objects.hash(ptr), guineaPig.hashCode());
    }

    @Test
    void verifyToString() {
        assumeGuineaPigPresent();
        ToStringVerifier.forClass(LibUsbDevice.class, guineaPig).verify();
    }

    @AfterAll
    static void exitContext() {
        /*
         * LibUSB allows a null value to represent the default context.
         * However, this module forbids the usage of the default context
         * for LibUSB. As such, assume this was a user mistake.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.exitContext(null));

        assumeContextPresent();
        LibUsbDevice.exitContext(context);
    }

}