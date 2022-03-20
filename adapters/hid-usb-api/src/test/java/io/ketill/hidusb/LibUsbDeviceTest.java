package io.ketill.hidusb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.opentest4j.TestAbortedException;
import org.usb4java.Context;
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
    void requireSuccess() {
        /*
         * It would not make sense to require a null operation to
         * succeed. As such, assume this was an error by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.requireSuccess(null));

        /*
         * Any value less than zero will result in a LibUsbException
         * being thrown by the LibUSB library. This exception must
         * be thrown back to the caller.
         */
        assertThrows(LibUsbException.class, () ->
                LibUsbDevice.requireSuccess(() -> LibUsb.ERROR_IO));

        /*
         * When a LibUSB operation is successful, its return code
         * should be returned to the caller. Not doing so could
         * make this method unusable in certain situations. The
         * generated number is given a positive bound to ensure
         * a negative value is never returned.
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
    void getZadigHomepage() {
        /*
         * While specified as a possibility in its documentation,
         * this method should never return null. If it does, that
         * means something has gone wrong. The null return value
         * is simply to prevent the application from crashing.
         */
        assertNotNull(LibUsbDevice.getZadigHomepage());
    }

    @Test
    void openZadigHomepage() throws IOException {
        try (MockedStatic<Desktop> awtDesktop = mockStatic(Desktop.class)) {
            /*
             * If Java AWT desktop is not supported, then opening
             * the home page for Zadig will not succeed. As such,
             * this method should return false.
             */
            awtDesktop.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(LibUsbDevice.openZadigHomepage());
            awtDesktop.when(Desktop::isDesktopSupported).thenReturn(true);

            Desktop desktop = mock(Desktop.class);
            awtDesktop.when(Desktop::getDesktop).thenReturn(desktop);

            /*
             * If browsing is not supported, then opening the home
             * page for Zadig will not be possible. As such, this
             * method should return false.
             */
            when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(false);
            assertFalse(LibUsbDevice.openZadigHomepage());
            when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

            /*
             * If calling browse() results in an I/O exception, it
             * means the home page  was not opened. As such, this
             * method should return false.
             */
            doThrow(new IOException()).when(desktop).browse(any());
            assertFalse(LibUsbDevice.openZadigHomepage());
            doNothing().when(desktop).browse(any());

            /*
             * If everything goes right when opening the home page,
             * this method should return true. It should have also
             * made a call to the browse().
             */
            assertTrue(LibUsbDevice.openZadigHomepage());
            verify(desktop, times(2)).browse(any());
        }


        try (MockedStatic<LibUsbDevice> libUsbDevice =
                     mockStatic(LibUsbDevice.class)) {
            libUsbDevice.when(LibUsbDevice::getZadigHomepage)
                    .thenReturn(null);

            /*
             * If the URI for the Zadig home page could not
             * be resolved, this method will fail. As such,
             * it should return false.
             */
            libUsbDevice.when(LibUsbDevice::openZadigHomepage)
                    .thenCallRealMethod();
            assertFalse(LibUsbDevice.openZadigHomepage());
        }
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
             * This LibUSB error can be ignored. Not all tests
             * require a valid context. Any tests which do will
             * check if it is present before proceeding.
             */
        }
    }

    @Test
    void getConnected() {
        /*
         * Using the default context (which is represented as null
         * when using LibUSB) is forbidden. Furthermore, a LibUSB
         * device supplier is required to instantiate wrappers for
         * scanned devices. If either of these are null, assume it
         * was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.getConnected(null, LibUsbDevice::new));
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.getConnected(context, null));

        assumeContextPresent();

        try {
            List<LibUsbDevice> connected
                    = LibUsbDevice.getConnected(context, LibUsbDevice::new);

            /*
             * If retrieving the connected devices succeeds, then
             * the returned list should never be null (it can of
             * course be empty.) If the list is not empty, none
             * of its elements should be null either.
             */
            assertNotNull(connected);
            for (LibUsbDevice device : connected) {
                assertNotNull(device);
            }

            /*
             * This next test can only be run if at least one USB
             * device is connected to the machine. If no devices
             * are connected, the nullability check will never be
             * reached (and thus result in a failing assertion.)
             */
            if (!connected.isEmpty()) {
                /*
                 * It would not make sense for the supplier of a
                 * LibUSB device to return a null value. As such,
                 * assume this was a mistake by the user.
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
    void closeDevices() {
        /*
         * It would not make sense to pass a null iterable of
         * devices to close, nor have a null device as one of
         * its elements. As such, assume this was a mistake by
         * the user and thrown an exception.
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
         * As the name of this method implies, it should close
         * all devices it is given (whether they are already
         * closed or not.) Failure to do so could result in a
         * memory leak.
         */
        LibUsbDevice.closeDevices(devices);
        for (LibUsbDevice device : devices) {
            verify(device).close();
        }
    }

    private LibUsbDevice guineaPig;

    private void assumeGuineaPigPresent() {
        assumeTrue(guineaPig != null, "no LibUSB device");
    }

    @BeforeEach
    void setup() {
        /*
         * Do not use assumeContextPresent() here. There are some
         * tests which do not rely on a context at all. Requiring
         * for it to be present in setup would mean they do not
         * run even though they don't require it.
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
         * Because LibUSB has proved more-or-less impossible to
         * mock, the next best thing is to just run unit tests
         * using a handle to a real device. All tests that rely
         * on a guinea pig device will simply be skipped if one
         * is not present.
         */
        if (connected != null && !connected.isEmpty()) {
            this.guineaPig = connected.get(0);
        }
    }

    @Test
    void usbContext() {
        assumeGuineaPigPresent();

        /*
         * The context of the device should match the context used
         * to create it during setup. If they do not match, assume
         * something has gone wrong during instantiation.
         */
        assertEquals(guineaPig.usbContext, context);
    }

    @Test
    void usbDevice() {
        assumeGuineaPigPresent();

        /*
         * If the underlying USB device is null, that means there
         * is nothing to perform I/O with. This makes the wrapper
         * a dead weight, and indicates something has gone wrong.
         */
        assertNotNull(guineaPig.usbDevice);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void getHandle() {
        assumeGuineaPigPresent();

        /*
         * If the USB handle has not been opened yet, assume this
         * was a mistake by the user and throw an exception. The
         * user is expected to open the USB handle themselves.
         */
        assertThrows(IllegalStateException.class,
                () -> guineaPig.getHandle());
    }

    @Test
    void openHandle() {
        assumeGuineaPigPresent();

        try {
            guineaPig.openHandle();
            assertNotNull(guineaPig.getHandle());
        } catch (LibUsbException e) {
            libUsbError(e);
        }
    }

    @Test
    void close() {
        assumeGuineaPigPresent();

        assertFalse(guineaPig.isClosed());
        guineaPig.close();
        assertTrue(guineaPig.isClosed());

        /*
         * When a LibUSB device wrapper is closed via close(), it
         * is expected to unreference its underlying device (and
         * as a result destroy it.) Attempting to reference the
         * device will confirm if it's been destroyed or not.
         */
        assertThrows(IllegalStateException.class,
                () -> LibUsb.refDevice(guineaPig.usbDevice));

        /*
         * It would not make sense to open a USB handle after this
         * device has been closed. As such, assume this was a user
         * mistake and throw an exception.
         */
        assertThrows(IllegalStateException.class, guineaPig::openHandle);

        /*
         * It is legal to call close() on a LibUSB device after
         * it has originally been closed. This is to fall in line
         * with the Closeable interface as provided by Java.
         */
        assertDoesNotThrow(guineaPig::close);
    }

    @Test
    @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself"})
    void testEquals() {
        assumeGuineaPigPresent();

        assertFalse(guineaPig.equals(null));
        assertFalse(guineaPig.equals(new Object()));
        assertFalse(guineaPig.equals(mock(LibUsbDevice.class)));
        assertTrue(guineaPig.equals(guineaPig));
    }

    @Test
    void testHashCode() {
        assumeGuineaPigPresent();

        long ptr = guineaPig.usbDevice.getPointer();
        assertEquals(Objects.hash(ptr), guineaPig.hashCode());
    }

    @AfterAll
    static void exitContext() {
        /*
         * While LibUSB allows for a null value to represent the
         * default context, this module forbids the use of the
         * default context for LibUSB. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> LibUsbDevice.exitContext(null));

        assumeContextPresent();
        LibUsbDevice.exitContext(context);
    }

}