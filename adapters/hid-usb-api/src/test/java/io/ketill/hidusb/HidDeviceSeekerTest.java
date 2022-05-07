package io.ketill.hidusb;

import io.ketill.KetillException;
import org.hid4java.HidDevice;
import org.hid4java.event.HidServicesEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HidDeviceSeekerTest {

    private HidDevice peripheral;
    private HidServicesEvent event;
    private MockHidDeviceSeeker seeker;

    @BeforeEach
    void createSeeker() {
        MockHidDeviceSeeker.scanWaitDisabled = true;

        this.peripheral = mock(HidDevice.class);
        this.event = mock(HidServicesEvent.class);

        when(peripheral.getVendorId()).thenReturn(0x1234);
        when(peripheral.getProductId()).thenReturn(0x5678);
        when(event.getHidDevice()).thenReturn(peripheral);

        this.seeker = new MockHidDeviceSeeker();
        seeker.targetProduct(peripheral.getVendorId(),
                peripheral.getProductId());
    }

    @Test
    void testInit() {
        /*
         * The internal HID services of Hid4Java uses an integer to store
         * the scan interval, as opposed to a long used by PeripheralSeeker.
         * If the value were too large, its value cast to an integer would
         * be incorrect and possibly much lower than what the user intended.
         * For the sake of the user, throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockHidDeviceSeeker(Long.MAX_VALUE));
    }

    @Test
    void testGetId() {
        ProductId id = seeker.getId(peripheral);
        assertEquals(peripheral.getVendorId(), id.vendorId);
        assertEquals(peripheral.getProductId(), id.productId);
    }

    @Test
    void testGetHash() {
        /*
         * Since HidDevice properly implements hashCode(), the HID device
         * seeker should return that value without doing more calculation.
         */
        int peripheralHash = peripheral.hashCode();
        assertEquals(peripheralHash, seeker.getHash(peripheral));
    }

    @Test
    void testHidDeviceAttached() {
        /* attach HID device for next test */
        seeker.hidDeviceAttached(event);

        /*
         * When an HID device is attached, the HID device seeker should add
         * it to the list of currently scanned peripherals.
         */
        Collection<HidDevice> scan = seeker.scanPeripherals();
        assertEquals(1, scan.size());
        assertTrue(scan.contains(peripheral));

        /* attach HID device again */
        seeker.hidDeviceAttached(event);

        /*
         * If Hid4Java for some reason says the HID device connects for a
         * second time, the seeker should catch this and not add it to the
         * current list of scanned peripherals.
         */
        Collection<HidDevice> sanityScan = seeker.scanPeripherals();
        assertEquals(1, sanityScan.size());
        assertTrue(sanityScan.contains(peripheral));

        /*
         * If an exception occurs in the listener method, it should be caught
         * and recorded. This exception must then be thrown on the next call
         * to seek(). Hid4Java silently suppresses all exceptions thrown in
         * listener methods, so this acts as a fallback in case of an error.
         */
        when(event.getHidDevice()).thenThrow(RuntimeException.class);
        seeker.hidDeviceAttached(event); /* trigger throw */
        assertThrows(KetillException.class, seeker::seek);
    }

    @Test
    void testHidDeviceDetached() {
        /*
         * For this test to be valid, the device must first be attached, and
         * then detached. Assuming device attachment is functional, it allows
         * the test for device detachment to properly execute.
         */
        seeker.hidDeviceAttached(event);
        seeker.hidDeviceDetached(event);

        /*
         * When an HID device is detached, the HID device seeker must remove
         * it from the list of currently scanned peripherals.
         */
        Collection<HidDevice> scan = seeker.scanPeripherals();
        assertEquals(0, scan.size());

        /*
         * If an exception occurs in the listener method, it should be caught
         * and recorded. This exception must then be thrown on the next call
         * to seek(). Hid4Java silently suppresses all exceptions thrown in
         * listener methods, so this acts as a fallback in case of an error.
         */
        when(event.getHidDevice()).thenThrow(RuntimeException.class);
        seeker.hidDeviceDetached(event); /* trigger throw */
        assertThrows(KetillException.class, seeker::seek);
    }

    @Test
    void testHidFailure() {
        /* attach HID device for next test */
        seeker.hidDeviceAttached(event);

        /*
         * When an HID device failure occurs, the seeker must block it until
         * detached or manually unblocked by the user. When a failure occurs,
         * it's unlikely communication will be possible with the HID device.
         */
        seeker.hidFailure(event);
        assertTrue(seeker.isPeripheralBlocked(peripheral));

        /*
         * If an exception occurs in the listener method, it should be caught
         * and recorded. This exception must then be thrown on the next call
         * to seek(). Hid4Java silently suppresses all exceptions thrown in
         * listener methods, so this acts as a fallback in case of an error.
         */
        when(event.getHidDevice()).thenThrow(RuntimeException.class);
        seeker.hidFailure(event); /* trigger throw */
        assertThrows(KetillException.class, seeker::seek);
    }

    @Test
    void testSetupPeripheral() {
        /* attach HID device for next test */
        seeker.hidDeviceAttached(event);

        /*
         * When a peripheral fails to open during setup, communication will
         * be impossible. As such, block the device until it is detached.
         * It is possible the peripheral will be successfully opened the
         * next time it is attached to the system.
         */
        when(peripheral.open()).thenReturn(false);
        seeker.seek(); /* scan peripherals */
        assertFalse(seeker.connectedPeripheral);
        assertTrue(seeker.isPeripheralBlocked(peripheral));

        /* unblock peripheral for next test */
        seeker.unblockPeripheral(peripheral);

        /*
         * When a peripheral is successfully opened, the seeker should have
         * set all I/O operations to be non-blocking. This is done for the
         * convenience of the user.
         */
        when(peripheral.open()).thenReturn(true);
        seeker.seek(); /* scan peripherals */
        assertTrue(seeker.connectedPeripheral);
        verify(peripheral).setNonBlocking(true);
    }

    @Test
    void testShutdownPeripheral() {
        /*
         * The code below attaches the mock peripheral to the HID device
         * seeker. This is required for the test to ensure the peripheral
         * was shutdown.
         */
        when(peripheral.open()).thenReturn(true);
        seeker.hidDeviceAttached(event);
        seeker.seek(); /* scan peripherals */

        /*
         * When an HID device is detached, the HID device seeker must close
         * it. Failing to do this would result in a memory leak.
         */
        seeker.hidDeviceDetached(event);
        seeker.seek(); /* scan peripherals */
        verify(peripheral).close();
    }

    @Test
    void testClose() {
        seeker.close();

        /*
         * When the HID device seeker is closed, it should no longer handle
         * any events coming from the HID device listener. As such, the HID
         * device should not be added to the list of peripherals.
         */
        seeker.hidDeviceAttached(event);
        Collection<HidDevice> scan = seeker.scanPeripherals();
        assertEquals(0, scan.size());

        /*
         * Since devices cannot be attached in this state, there's nothing
         * to check for here. This is called to ensure event.getHidDevice()
         * is not called by the seeker when ending this test.
         */
        seeker.hidDeviceDetached(event);

        /*
         * When the HID device seeker is closed, it should no longer handle
         * any events coming from the HID device listener. As such, the HID
         * device should not be blocked after calling this method.
         */
        seeker.hidFailure(event);
        assertFalse(seeker.isPeripheralBlocked(peripheral));

        /*
         * Since all events were ignored, the HID device seeker should have
         * never called getHidDevice() on the event object.
         */
        verify(event, never()).getHidDevice();
    }

}