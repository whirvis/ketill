package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceObserverTest {

    private IoDevice device;
    private Subject<IoDeviceEvent> subject;
    private IoDeviceObserver observer;

    @BeforeEach
    void createObserver() {
        this.device = mock(IoDevice.class);
        this.subject = PublishSubject.create();
        this.observer = new IoDeviceObserver(device, subject);
    }

    @Test
    void testGetDevice() {
        assertSame(device, observer.getDevice());
    }

    @Test
    void testOnNext() {
        /*
         * It would not make sense to emit a null event from the device.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> observer.onNext(null));

        /*
         * It would not make sense to emit an event from a device other than
         * the one that created the observer. As such, assume this was a user
         * mistake and throw an exception.
         */
        MockIoDeviceEvent foreignEvent =
                new MockIoDeviceEvent(mock(IoDevice.class));
        assertThrows(IllegalArgumentException.class,
                () -> observer.onNext(foreignEvent));

        MockIoDeviceEvent houseEvent = new MockIoDeviceEvent(device);
        AtomicBoolean emitted = new AtomicBoolean();
        Disposable subscription = subject.subscribe(event -> {
            boolean inHouse = event == houseEvent;
            emitted.set(inHouse);
        });

        observer.onNext(houseEvent);
        assertTrue(emitted.get());
        subscription.dispose();
    }

}
