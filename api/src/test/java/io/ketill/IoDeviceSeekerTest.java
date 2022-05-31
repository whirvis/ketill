package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceSeekerTest {

    private MockIoDeviceSeeker seeker;
    private MockIoDevice device;

    @BeforeEach
    void createSeeker() {
        this.seeker = new MockIoDeviceSeeker();
        this.device = new MockIoDevice();
    }

    @Test
    void testSubscribeEvents() {
        /*
         * It makes no sense to subscribe for events of a null type or to
         * subscribe for events with a null callback. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        /* @formatter:off */
        assertThrows(NullPointerException.class,
                () -> seeker.subscribeEvents(null, event -> {}));
        assertThrows(NullPointerException.class,
                () -> seeker.subscribeEvents(null));
        /* @formatter:on */

        AtomicBoolean emitted = new AtomicBoolean();
        Disposable subscription = seeker.subscribeEvents(event -> {
            IoDeviceSeeker<?> emitter = event.getSeeker();
            emitted.set(emitter == seeker);
        });

        /*
         * Once subscribed, the I/O device seeker should return a subscription
         * that can later be disposed of. Furthermore, since no type was given,
         * the argument for the eventClazz parameter should have defaulted
         * to IoDeviceSeekerEvent.class. As such, any emitted events should
         * result in the callback being executed.
         */
        assertNotNull(subscription);
        seeker.observer.onNext(new MockIoDeviceSeekerEvent(seeker));
        assertTrue(emitted.get());

        /* reset emitted for next test */
        emitted.set(false);

        /*
         * After the subscription has been disposed of, the callback should
         * no longer be executed.
         */
        subscription.dispose();
        seeker.observer.onNext(new MockIoDeviceSeekerEvent(seeker));
        assertFalse(emitted.get());
    }

    @Test
    void testDiscoverDevice() {
        AtomicBoolean discovered = new AtomicBoolean();
        seeker.subscribeEvents(IoDeviceDiscoverEvent.class,
                event -> discovered.set(event.getDevice() == device));

        /*
         * When a device is first discovered, the device seeker is expected
         * to execute the internal hook as well as emit an event.
         */
        seeker.discoverDevice(device);
        assertTrue(seeker.discoveredDevice);
        assertTrue(discovered.get());
        assertEquals(1, seeker.getDeviceCount());

        /* reset state for next test */
        seeker.discoveredDevice = false;
        discovered.set(false);

        /*
         * Once a device has been discovered, it would not make sense for it
         * to be discovered As such, the event should not be re-emitted.
         */
        seeker.discoverDevice(device);
        assertFalse(seeker.discoveredDevice);
        assertFalse(discovered.get());

        /*
         * It would not make sense to discover a null device. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.discoverDevice(null));
    }

    @Test
    void testForgetDevice() {
        AtomicBoolean forgotten = new AtomicBoolean();
        seeker.subscribeEvents(IoDeviceForgetEvent.class,
                event -> forgotten.set(event.getDevice() == device));

        /* discover device for next test */
        seeker.discoverDevice(device);

        /*
         * When a discovered device is forgotten, the device seeker should
         * execute the internal hook method and then emit an event.
         */
        seeker.forgetDevice(device);
        assertTrue(seeker.forgotDevice);
        assertTrue(forgotten.get());
        assertEquals(0, seeker.getDeviceCount());

        /* reset state for next test */
        seeker.forgotDevice = false;
        forgotten.set(false);

        /*
         * If a device was not previously discovered, it would not make sense
         * to forget it. As such, no event should be emitted.
         */
        seeker.forgetDevice(device);
        assertFalse(seeker.forgotDevice);
        assertFalse(forgotten.get());

        /*
         * It would not make sense to forget a null device. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.forgetDevice(null));
    }

    @Test
    void testGetDeviceCount() {
        assertEquals(0, seeker.getDeviceCount());
    }

    @Test
    void testGetDevices() {
        /*
         * Since no devices have been discovered yet, the returned list
         * should be empty. Furthermore, the returned list is read-only.
         * As such, any modifications should result in an exception.
         */
        List<MockIoDevice> devices = seeker.getDevices();
        assertTrue(devices.isEmpty());
        assertThrows(UnsupportedOperationException.class, devices::clear);
    }

    @Test
    void testForEachDevice() {
        seeker.discoverDevice(device);
        seeker.forEachDevice(MockIoDevice::executeTask);
        assertTrue(device.executedTask);
    }

    @Test
    void testPollDevices() {
        seeker.discoverDevice(device);
        seeker.pollDevices();
        assertTrue(device.polled);
    }

    @Test
    void testSeek() {
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
    void testSeekerError() {
        seeker.errorOnSeek = true;

        /*
         * When an error occurs while seeking for devices, the device seeker
         * is obligated to wrap the exception throw it back to the caller.
         * This ensures errors do not occur silently.
         */
        assertThrows(KetillException.class, seeker::seek);
    }

    @Test
    void testClose() {
        /* discover device for next test */
        seeker.discoverDevice(device);

        /*
         * When a device seeker is closed, it is expected forget all devices
         * that were previously discovered. This is because they will usually
         * no longer be used.
         */
        AtomicBoolean forgotten = new AtomicBoolean();
        assertFalse(seeker.isClosed());
        seeker.subscribeEvents(IoDeviceForgetEvent.class,
                event -> forgotten.set(event.getDevice() == device));

        seeker.close();

        assertTrue(forgotten.get());
        assertTrue(seeker.isClosed());

        /*
         * It would not make sense to discover a device, forget a device, or
         * perform a device scan after the device seeker has been closed.
         * Assume this was mistake by the user and throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> seeker.discoverDevice(device));
        assertThrows(IllegalStateException.class,
                () -> seeker.forgetDevice(device));
        assertThrows(IllegalStateException.class, seeker::seek);

        /*
         * It is legal to call close() on an I/O device seeker after it has
         * originally been closed. This is to fall in line with the Closeable
         * interface as provided by Java.
         */
        assertDoesNotThrow(seeker::close);
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(IoDeviceSeeker.class, seeker);
    }

}