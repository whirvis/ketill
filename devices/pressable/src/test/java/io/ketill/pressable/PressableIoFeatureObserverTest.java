package io.ketill.pressable;

import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class PressableIoFeatureObserverTest {

    private MockIoDevice device;
    private MockIoFeature feature;
    private MockPressableState state;
    private MockIoFeatureObserver observer;

    @BeforeEach
    void createObserver() {
        this.device = new MockIoDevice();
        this.feature = new MockIoFeature();
        this.state = device.registerFeature(feature).getState();
        this.observer = state.observer;
    }

    @Test
    void testInit() {
        /*
         * It would not make sense to create a pressable I/O feature with
         * a I/O feature, internal state, or I/O device observer. As such,
         * assume these were mistakes by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureObserver(null, state,
                        device.eventsAccess));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureObserver(feature, null,
                        device.eventsAccess));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureObserver(feature, state, null));
    }

    @Test
    void testIsPressed() {
        assertFalse(observer.isPressed());
    }

    @Test
    void testIsHeld() {
        assertFalse(observer.isHeld());
    }

    @Test
    void testGetConfig() {
        /*
         * Since the I/O device used for the other tests does not implement
         * the PressableIoFeatureSupport interface, the observer should use
         * the default configuration.
         */
        assertSame(PressableIoFeatureConfig.DEFAULT, observer.getConfig());

        /* create device with pressable support for next test */
        MockIoDevice.WithSupport support = new MockIoDevice.WithSupport();
        PressableIoFeatureObserver<?> impl =
                support.registerFeature(feature).getState().observer;
        PressableIoFeatureConfig custom = new PressableIoFeatureConfig();

        /*
         * When an I/O device supports pressable I/O features, the observer
         * should use the config that is returned by the device. This allows
         * users to tweak, or even fully disable, features like holding.
         */
        support.usePressableConfig(custom);
        assertSame(custom, impl.getConfig());
        assertTrue(support.requestedConfig);

        /*
         * However, if the device returns null when a config is requested,
         * the default configuration should be used instead. This prevents
         * an unnecessary exception from being thrown.
         */
        support.requestedConfig = false;
        support.usePressableConfig(null);
        assertSame(PressableIoFeatureConfig.DEFAULT, impl.getConfig());
        assertTrue(support.requestedConfig);
    }

    @Test
    void testOnSubscribe() {
        /*
         * This observer implements the methods required by the interface
         * by calling the methods implemented in IoDeviceObserver. Since
         * it does not support onSubscribe(), this should also result in
         * an UnsupportedOperationException being thrown.
         */
        Disposable disposable = mock(Disposable.class);
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onSubscribe(disposable));
    }

    @Test
    void testOnNext() {
        /*
         * It would not make sense to emit a null event from this observer.
         * As such, assume it was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> observer.onNext(null));

        /*
         * It would not make sense to emit an event created for a different
         * device than the one this observer was created for. Assume this
         * was a mistake by the user and throw an exception.
         */
        IoFeaturePressEvent foreignEvent = new IoFeaturePressEvent(device,
                new MockIoFeature());
        assertThrows(IllegalArgumentException.class,
                () -> observer.onNext(foreignEvent));

        /* subscribe to observer for next test */
        AtomicBoolean emitted = new AtomicBoolean();
        device.subscribeEvents(PressableIoFeatureEvent.class,
                event -> emitted.set(true));

        /*
         * When a valid event is passed to the observer, it should emit
         * it to subscribers. Failure to do so indicates an error.
         */
        IoFeaturePressEvent houseEvent = new IoFeaturePressEvent(device,
                feature);
        observer.onNext(houseEvent);
        assertTrue(emitted.get());
    }

    @Test
    void testOnError() {
        /*
         * This observer implements the methods required by the interface
         * by calling the methods implemented in IoDeviceObserver. Since
         * it does not support onError(), this should also result in an
         * UnsupportedOperationException being thrown.
         */
        Throwable cause = new Throwable();
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onError(cause));
    }

    @Test
    void testOnComplete() {
        /*
         * This observer implements the methods required by the interface
         * by calling the methods implemented in IoDeviceObserver. Since
         * it does not support onComplete(), this should also result in
         * an UnsupportedOperationException being thrown.
         */
        assertThrows(UnsupportedOperationException.class,
                () -> observer.onComplete());
    }

    @Test
    void testOnPress() {
        /* subscribe to observer for next test */
        AtomicBoolean emitted = new AtomicBoolean();
        device.subscribeEvents(IoFeaturePressEvent.class,
                event -> emitted.set(true));

        /*
         * By default, the pressable I/O feature observer should emit
         * an IoFeaturePressEvent when onPress() is invoked.
         */
        observer.onPress();
        assertTrue(emitted.get());
    }

    @Test
    void testOnHold() {
        /* subscribe to observer for next test */
        AtomicBoolean emitted = new AtomicBoolean();
        device.subscribeEvents(IoFeatureHoldEvent.class,
                event -> emitted.set(true));

        /*
         * By default, the pressable I/O feature observer should emit
         * an IoFeatureHoldEvent when onHold() is invoked.
         */
        observer.onHold();
        assertTrue(emitted.get());
    }

    @Test
    void testOnRelease() {
        /* subscribe to observer for next test */
        AtomicBoolean emitted = new AtomicBoolean();
        device.subscribeEvents(IoFeatureReleaseEvent.class,
                event -> emitted.set(true));

        /*
         * By default, the pressable I/O feature observer should emit
         * an IoFeatureReleaseEvent when onRelease() is invoked.
         */
        observer.onRelease();
        assertTrue(emitted.get());
    }

    @Test
    void testEmitPressEvents() {
        /* subscribe to observer for next test */
        AtomicBoolean emittedPress = new AtomicBoolean();
        AtomicBoolean emittedRelease = new AtomicBoolean();
        device.subscribeEvents(IoFeaturePressEvent.class,
                event -> emittedPress.set(true));
        device.subscribeEvents(IoFeatureReleaseEvent.class,
                event -> emittedRelease.set(true));

        /*
         * Since the state is not pressed, polling the observer the first
         * time should not result in an IoFeaturePressEvent being emitted.
         * However, since the feature was not pressed earlier, it should
         * not emit an IoFeatureReleaseEvent either.
         */
        observer.poll();
        assertFalse(emittedPress.get());
        assertFalse(emittedRelease.get());

        /*
         * When the state of an I/O feature indicates it is pressed, the
         * I/O feature observer should emit an IoFeaturePressEvent on the
         * first poll it notices the change in state.
         */
        state.pressed = true;
        observer.poll();
        assertTrue(observer.isPressed());
        assertTrue(emittedPress.get());

        /*
         * Once an IoFeaturePressEvent has been emitted by the observer,
         * it should not be emitted again until the feature is released
         * and then pressed again.
         */
        emittedPress.set(false);
        observer.poll();
        assertFalse(emittedPress.get());

        /*
         * When the state of an I/O feature indicates it is released, the
         * I/O feature observer should emit an IoFeaturePressEvent on the
         * first poll it notices the change in state.
         */
        state.pressed = false;
        observer.poll();
        assertFalse(observer.isPressed());
        assertTrue(emittedRelease.get());

        /*
         * Once an IoFeatureReleaseEvent has been emitted by the observer,
         * it should not be emitted again until the feature is pressed and
         * then released again.
         */
        emittedRelease.set(false);
        observer.poll();
        assertFalse(emittedPress.get());
    }

    @Test
    void testEmitHoldEvents() throws InterruptedException {
        /*
         * Before subscribing to the observer, trigger the press event by
         * setting the state to indicate it is pressed down and polling
         * the observer. This ensures that emittedPress will not be set
         * to a value of true too early in the test.
         */
        state.pressed = true;
        observer.poll();

        /* subscribe to observer for next test */
        AtomicBoolean emittedPress = new AtomicBoolean();
        AtomicBoolean emittedHold = new AtomicBoolean();
        device.subscribeEvents(IoFeaturePressEvent.class,
                event -> emittedPress.set(true));
        device.subscribeEvents(IoFeatureHoldEvent.class,
                event -> emittedHold.set(true));

        /* do a little trolling */
        PressableIoFeatureConfig config =
                (PressableIoFeatureConfig) observer.getConfig();

        /*
         * After a feature has been held down for a time greater than or
         * equal to the hold time specified by the config, the observer
         * should emit both an IoFeatureHoldEvent and IoFeaturePressEvent.
         */
        Thread.sleep(config.getHoldTime());
        observer.poll();
        assertTrue(observer.isHeld());
        assertTrue(emittedPress.get());
        assertTrue(emittedHold.get());

        /*
         * If not enough time has passed between polls when the feature is
         * held down, an IoFeaturePressEvent should not be emitted. Since
         * the default config (which is used for testing here) requires one
         * hundred milliseconds to elapse, no events should be emitted.
         */
        emittedPress.set(false);
        observer.poll();
        assertFalse(emittedPress.get());

        /*
         * After enough time has elapsed, the observer should emit another
         * IoFeaturePressEvent as the feature is being held down.
         */
        Thread.sleep(config.getHoldPressInterval());
        observer.poll();
        assertTrue(emittedPress.get());

        /*
         * The configuration can indicate that holdable features are not
         * supported by the device. When this occurs, the observer should
         * not consider the feature held down (even if it was previously
         * considered to be held down).
         */
        config.setHoldTime(PressableIoFeatureConfig.DISABLE_HOLD);
        observer.poll();
        assertFalse(observer.isHeld());
    }

}
