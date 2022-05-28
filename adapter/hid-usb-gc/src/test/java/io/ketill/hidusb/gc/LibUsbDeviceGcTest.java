package io.ketill.hidusb.gc;

import io.ketill.hidusb.LibUsbDevice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@SuppressWarnings("ConstantConditions")
class LibUsbDeviceGcTest {

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

    private MockLibUsbDeviceGc guineaPig;

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

        List<MockLibUsbDeviceGc> connected = null;
        try {
            connected = LibUsbDevice.getConnected(context,
                    MockLibUsbDeviceGc::new);
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
    void testFillInterruptTransfer() {
        assumeGuineaPigPresent();

        Transfer transfer = LibUsb.allocTransfer();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1);
        byte endpoint = (byte) 0x00;

        assertThrows(NullPointerException.class,
                () -> guineaPig.fillInterruptTransfer(null, endpoint,
                        buffer, null, null,0L));
        assertThrows(NullPointerException.class,
                () -> guineaPig.fillInterruptTransfer(transfer, endpoint,
                        null, null, null,0L));
        assertThrows(IllegalArgumentException.class,
                () -> guineaPig.fillInterruptTransfer(transfer, endpoint,
                        buffer, null, null,-1L));



        assertThrows(IllegalStateException.class,
                () -> guineaPig.fillInterruptTransfer(transfer, endpoint,
                        buffer, null, null, 0L));

        guineaPig.close();

        assertThrows(IllegalStateException.class,
                () -> guineaPig.fillInterruptTransfer(transfer, endpoint,
                        buffer, null, null, 0L));

        LibUsb.freeTransfer(transfer);
    }

    @Test
    void testInterruptTransfer() {
        assumeGuineaPigPresent();

        Transfer transfer = LibUsb.allocTransfer();
        byte endpoint = (byte) 0x00;
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);

        assertThrows(IllegalStateException.class,
                () -> guineaPig.interruptTransfer(endpoint, buffer, 0L));

        guineaPig.openThing();
        
    }

    @Test
    void testSubmitTransfer() {

    }

    @Test
    void testHandleEventsCompleted() {

    }

    @Test
    void testClaimInterface() {

    }

    @Test
    void testClose() {

    }

}
