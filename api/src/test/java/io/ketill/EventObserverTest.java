package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class EventObserverTest {

    private MockEventObserver observer;

    @BeforeEach
    void createObserver() {
        this.observer = new MockEventObserver(PublishSubject.create());
    }

    @Test
    void testOnSubscribe() {
        Disposable disposable = mock(Disposable.class);
        assertThrows(NullPointerException.class,
                () -> observer.onSubscribe(null));
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onSubscribe(disposable));
    }

    @Test
    void testOnNext() {
        assertDoesNotThrow(() -> observer.onNext("event"));
    }

    @Test
    void testOnError() {
        Throwable cause = new Throwable();
        assertThrows(NullPointerException.class,
                () -> observer.onError(null));
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onError(cause));
    }

    @Test
    void testOnComplete() {
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onComplete());
    }

}
