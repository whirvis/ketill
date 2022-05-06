package io.ketill.pressable;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class PressableFeatureMonitorTest {

    private MockIoDevice device;
    private MockIoFeature feature;
    private Object state;
    private Consumer<PressableFeatureEvent> callback;
    private Supplier<Consumer<PressableFeatureEvent>> supplier;
    private MockIoFeatureMonitor monitor;

    private boolean firedAfterPoll(@NotNull PressableFeatureEventType type,
                                   boolean held) {
        AtomicBoolean firedEvent = new AtomicBoolean();

        /* @formatter:off */
        this.callback = (event) -> {
            if (event.type == type && event.device == device
                    && event.feature == feature && event.held == held) {
                firedEvent.set(true);
            }

            /* ensure hook was called */
            assertSame(monitor.lastEvent, event);
        };
        /* @formatter:on */

        monitor.poll();
        return firedEvent.get();
    }

    private void assertFired(@NotNull PressableFeatureEventType type,
                             boolean held) {
        assertTrue(firedAfterPoll(type, held));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotFired(@NotNull PressableFeatureEventType type,
                                boolean held) {
        assertFalse(firedAfterPoll(type, held));
    }

    @BeforeEach
    void createMonitor() {
        this.device = new MockIoDevice();
        this.feature = new MockIoFeature();

        device.registerFeature(feature);

        this.state = device.getState(feature);
        this.supplier = () -> callback;
        this.monitor = new MockIoFeatureMonitor(device, feature, state,
                supplier);
    }

    @Test
    void testInit() {
        /*
         * It would not make sense for the device, feature, internal state,
         * or callback supplier to be null. assume this was a mistake by
         * the user and throw an exception.
         */
        /* @formatter:off */
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(null, feature, state,
                        supplier));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(device, null, state,
                        supplier));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(device, feature, null,
                        supplier));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(device, feature, state,
                        null));
        /* @formatter:on */

        /*
         * It would not make sense to supply an internal state which does
         * not belong to the feature. Assume this was a user mistake and
         * throw an exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoFeatureMonitor(device, feature, new Object(),
                        supplier));

        /* unregister feature for next test */
        device.unregisterFeature(feature);

        /*
         * It would not make sense to monitor a feature which has not been
         * registered to the specified device. As such, assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> new MockIoFeatureMonitor(device, feature, state,
                        supplier));
    }

    @Test
    void testPressEvents() {
        /*
         * When a feature is first pressed, the feature monitor should
         * fire a PRESS type event. Failure to do so indicates an error.
         */
        monitor.pressed = true;
        assertFired(PressableFeatureEventType.PRESS, false);

        /*
         * When a feature is finally released, the feature monitor should
         * fire a RELEASE type event. Failure to do so indicates an error.
         */
        monitor.pressed = false;
        assertFired(PressableFeatureEventType.RELEASE, false);
    }

    @Test
    void testHoldEvents() throws InterruptedException {
        PressableFeatureConfig config = new PressableFeatureConfig();
        device.usePressableConfig(config);

        /*
         * After a feature is held down for a time greater than or equal to
         * the hold time of the pressable config, the monitor should fire a
         * HOLD type event.
         */
        monitor.pressed = true;
        monitor.poll(); /* tell monitor feature is pressed */
        Thread.sleep(config.getHoldTime());
        assertFired(PressableFeatureEventType.HOLD, true);

        /*
         * When a feature is held down, the feature monitor should fire a
         * PRESS type event periodically in accordance to the pressable
         * config's hold press interval. Furthermore, if the configured
         * time has not elapsed, feature monitor should not fire a PRESS
         * type event.
         */
        Thread.sleep(config.getHoldPressInterval());
        assertFired(PressableFeatureEventType.PRESS, true);
        Thread.sleep(config.getHoldPressInterval() / 8);
        assertNotFired(PressableFeatureEventType.PRESS, true);

        /*
         * If feature holding is disabled, the feature monitor should not
         * fire PRESS type events. This is because a feature must be held
         * down before periodic feature presses can occur (even if they
         * are enabled).
         */
        config.setHoldTime(PressableFeatureConfig.DISABLE_HOLD);
        Thread.sleep(config.getHoldPressInterval());
        assertNotFired(PressableFeatureEventType.PRESS, true);
    }

}
