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
class IoDeviceSeekerObserverTest {

    private IoDeviceSeeker<?> seeker;
    private Subject<IoDeviceSeekerEvent> subject;
    private IoDeviceSeekerObserver observer;

    @BeforeEach
    void createObserver() {
        this.seeker = mock(IoDeviceSeeker.class);
        this.subject = PublishSubject.create();
        this.observer = new IoDeviceSeekerObserver(seeker, subject);
    }

    @Test
    void testGetSeeker() {
        assertSame(seeker, observer.getSeeker());
    }

    @Test
    void testOnNext() {
        /*
         * It would not make sense to emit a null event from the device seeker.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> observer.onNext(null));

        /*
         * It would not make sense to emit an event from a device seeker other
         * than the one that created the observer. As such, assume this was a
         * user mistake and throw an exception.
         */
        MockIoDeviceSeekerEvent foreignEvent =
                new MockIoDeviceSeekerEvent(mock(IoDeviceSeeker.class));
        assertThrows(IllegalArgumentException.class,
                () -> observer.onNext(foreignEvent));

        MockIoDeviceSeekerEvent houseEvent =
                new MockIoDeviceSeekerEvent(seeker);
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
