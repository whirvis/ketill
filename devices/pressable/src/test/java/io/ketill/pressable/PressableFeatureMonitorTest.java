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
    void setup() {
        this.device = new MockIoDevice();
        this.feature = new MockIoFeature();
        device.registerFeature(feature);

        this.supplier = () -> callback;
        this.monitor = new MockIoFeatureMonitor(device, feature, supplier);
    }

    @Test
    void init() {
        /*
         * It would not make sense for the device, feature,
         * or callback supplier to be null. As such, assume
         * this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(null, feature, supplier));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(device, null, supplier));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeatureMonitor(device, feature, null));

        /* unregister feature for next test */
        device.unregisterFeature(feature);

        /*
         * It would not make sense to monitor a feature which
         * has not been registered to the specified device.
         * As such, assume this was a mistake by the user and
         * throw an exception.
         */
        assertThrows(IllegalStateException.class,
                () -> new MockIoFeatureMonitor(device, feature, supplier));
    }

    @Test
    void firePressEvents() {
        /*
         * When a feature is first pressed, the feature
         * monitor is expected to fire a PRESS type event.
         * Failure to do so here indicates an error.
         */
        monitor.pressed = true;
        assertFired(PressableFeatureEventType.PRESS, false);

        /*
         * When a feature is finally released, the feature
         * monitor is expected to fire a RELEASE type event.
         * Failure to do so here indicates an error.
         */
        monitor.pressed = false;
        assertFired(PressableFeatureEventType.RELEASE, false);
    }

    @Test
    void fireHoldEvents() throws InterruptedException {
        PressableFeatureConfig config = new PressableFeatureConfig();
        device.usePressableConfig(config);

        /*
         * After a feature is held down for a time greater
         * than or equal to the hold time of the device's
         * pressable config, the monitor is expected to
         * fire a HOLD type event. Failure to do so here
         * indicates an error.
         */
        monitor.pressed = true;
        monitor.poll(); /* tell monitor feature is pressed */
        Thread.sleep(config.getHoldTime());
        assertFired(PressableFeatureEventType.HOLD, true);

        /*
         * When a feature is held down, the feature monitor
         * is expected to fire a PRESS type event periodically
         * in accordance to the pressable config's hold press
         * interval. Failure to do so here indicates an error.
         *
         * Furthermore, if the configured time has not elapsed,
         * the feature monitor is not expected to fire a PRESS
         * type event. If this occurs, it indicates an error.
         */
        Thread.sleep(config.getHoldPressInterval());
        assertFired(PressableFeatureEventType.PRESS, true);
        Thread.sleep(config.getHoldPressInterval() / 8);
        assertNotFired(PressableFeatureEventType.PRESS, true);

        /*
         * If feature holding is disabled, the feature monitor
         * is not expected to fire PRESS type events. This is
         * because a feature must be held down before periodic
         * feature presses can occur (even if they are enabled).
         * If it occurs here, it indicates an error.
         */
        config.setHoldTime(PressableFeatureConfig.DISABLE_HOLD);
        Thread.sleep(config.getHoldPressInterval());
        assertNotFired(PressableFeatureEventType.PRESS, true);
    }

}
