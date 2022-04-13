package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceSeekerTest {

    private MockIoDeviceSeeker seeker;
    private MockIoDevice device;

    @BeforeEach
    void setup() {
        this.seeker = new MockIoDeviceSeeker();
        this.device = new MockIoDevice();
    }

    @Test
    void discoverDevice() {
        AtomicBoolean discovered = new AtomicBoolean();
        seeker.onDiscoverDevice((s, d) -> discovered.set(d == device));

        /*
         * When a device is first discovered, the device seeker is expected
         * to execute the internal hook as well as the callback (assuming
         * one was set by the user). Failure to do so indicates an error.
         */
        seeker.discoverDevice(device);
        assertTrue(seeker.discoveredDevice);
        assertTrue(discovered.get());

        /* reset state for next test */
        seeker.discoveredDevice = false;
        discovered.set(false);

        /*
         * Once a device has been discovered, it would not make sense for it
         * to be discovered again (unless previously forgotten.) As such, the
         * callback should not be called again.
         */
        seeker.discoverDevice(device);
        assertFalse(seeker.discoveredDevice);
        assertFalse(discovered.get());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the seeker.
         */
        assertDoesNotThrow(() -> seeker.onDiscoverDevice(null));

        /*
         * It would not make sense to discover a null device. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.discoverDevice(null));
    }

    @Test
    void forgetDevice() {
        AtomicBoolean forgotten = new AtomicBoolean();
        seeker.onForgetDevice((s, d) -> forgotten.set(d == device));

        /* discover device for next test */
        seeker.discoverDevice(device);

        /*
         * When a discovered device is forgotten, the device seeker is
         * expected to execute the internal hook as well as the callback
         * (assuming one was set by the user). Failure to do so indicates
         * an error.
         */
        seeker.forgetDevice(device);
        assertTrue(seeker.forgotDevice);
        assertTrue(forgotten.get());

        /* reset state for next test */
        seeker.forgotDevice = false;
        forgotten.set(false);

        /*
         * If a device has is not discovered, it would not make sense to
         * forget it. As such, the callback should not be executed.
         */
        seeker.forgetDevice(device);
        assertFalse(seeker.forgotDevice);
        assertFalse(forgotten.get());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the seeker.
         */
        assertDoesNotThrow(() -> seeker.onDiscoverDevice(null));

        /*
         * It would not make sense to forget a null device. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.forgetDevice(null));
    }

    @Test
    void forEachDevice() {
        seeker.discoverDevice(device);
        seeker.forEachDevice(MockIoDevice::executeTask);
        assertTrue(device.executedTask);
    }

    @Test
    void pollDevices() {
        seeker.discoverDevice(device);
        seeker.pollDevices();
        assertTrue(device.polled);
    }

    @Test
    void seekImpl() {
        /*
         * When the seek() method is called, it must call the seekImpl()
         * method which is implemented by extending classes. This wrapping
         * is done to allow any exception to be thrown by the seeker. In
         * this test however, the implementation throws no exceptions.
         */
        seeker.seek();
        assertTrue(seeker.seeked);
    }

    @Test
    void seekerError() {
        seeker.errorOnSeek = true;

        /*
         * When no error callback is set, a device seeker is obligated to
         * wrap the exception it encounters and throw it back. This is to
         * ensure errors do not occur silently.
         */
        assertThrows(KetillException.class, seeker::seek);
        assertTrue(seeker.caughtSeekError);

        /* reset state for next test */
        seeker.caughtSeekError = false;

        /*
         * Once an error callback is set, the device seeker must not throw
         * the exception it encounters in seek(). Rather, it must notify the
         * callback of the error that has occurred and pass the exception.
         */
        AtomicBoolean caughtError = new AtomicBoolean();
        seeker.onSeekError((s, e) -> caughtError.set(true));
        assertDoesNotThrow(seeker::seek);
        assertTrue(seeker.caughtSeekError);
        assertTrue(caughtError.get());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the seeker.
         */
        assertDoesNotThrow(() -> seeker.onSeekError(null));
    }

    @Test
    void close() {
        /* discover device for next test */
        seeker.discoverDevice(device);

        /*
         * When a device seeker is closed, it is expected forget
         * all previously discovered devices. This is because they
         * will (usually) no longer be used.
         */
        AtomicBoolean forgotten = new AtomicBoolean();
        assertFalse(seeker.isClosed());
        seeker.onForgetDevice((s, d) -> forgotten.set(d == device));

        seeker.close();

        assertTrue(forgotten.get());
        assertTrue(seeker.isClosed());

        /*
         * It would not make sense to set any callbacks after the
         * device seeker has been closed. None of them will ever
         * be executed. As such, assume this was a mistake by the
         * user and thrown an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.onDiscoverDevice(null));
        assertThrows(IllegalStateException.class,
                () -> seeker.onForgetDevice(null));
        assertThrows(IllegalStateException.class,
                () -> seeker.onSeekError(null));

        /*
         * It would not make sense to discover a device, forget
         * a device, or perform a device scan after the device
         * seeker has been closed. Assume this was mistake by
         * the user and throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.discoverDevice(device));
        assertThrows(IllegalStateException.class,
                () -> seeker.forgetDevice(device));
        assertThrows(IllegalStateException.class, seeker::seek);

        /*
         * It is legal to call close() on an I/O device seeker after
         * it has originally been closed. This is to fall in line
         * with the Closeable interface as provided by Java.
         */
        assertDoesNotThrow(seeker::close);
    }

}