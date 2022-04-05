package io.ketill.pc;

import io.ketill.IoFeature;
import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.ketill.pc.Mouse.*;
import static org.junit.jupiter.api.Assertions.*;

class MouseTest {

    private Mouse mouse;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, mouse.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.mouse = new Mouse(MockPcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(mouse.m1, BUTTON_M1);
        assertStateIsFeature(mouse.m2, BUTTON_M2);
        assertStateIsFeature(mouse.m3, BUTTON_M3);
        assertStateIsFeature(mouse.m4, BUTTON_M4);
        assertStateIsFeature(mouse.m5, BUTTON_M5);
        assertStateIsFeature(mouse.m6, BUTTON_M6);
        assertStateIsFeature(mouse.m7, BUTTON_M7);
        assertStateIsFeature(mouse.m8, BUTTON_M8);

        assertSame(mouse.left, mouse.m1);
        assertSame(mouse.right, mouse.m2);
        assertSame(mouse.middle, mouse.m3);

        assertStateIsFeature(mouse.cursor, FEATURE_CURSOR);
    }

    @Test
    void onMouseButtonEvent() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        mouse.onPressableEvent(e -> notified.set(true));

        /*
         * When a mouse button is registered to a mouse,
         * the mouse is expected to notify listeners when
         * said button is clicked or unclicked.
         */
        MouseButton button = new MouseButton("button");
        Click1b state = mouse.registerFeature(button).state;

        state.clicked = true; /* click button */
        mouse.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When a mouse button unregistered from a mouse,
         * the mouse should no longer notify listeners when
         * said button is clicked or unclicked.
         */
        mouse.unregisterFeature(button);
        notified.set(false);

        state.clicked = false; /* unclick button */
        mouse.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void getPressableCallback() {
        /*
         * By default, the mouse should have no pressable
         * feature callback set unless one is provided by
         * the user via onPressableEvent().
         */
        assertNull(mouse.getPressableCallback());
    }

    @Test
    void usePressableCallback() {
        /*
         * After setting the pressable feature callback, the
         * mouse should use the one provided by the user.
         */
        /* @formatter:off */
        Consumer<PressableFeatureEvent> callback = (e) -> {};
        mouse.onPressableEvent(callback);
        assertSame(callback, mouse.getPressableCallback());
        /* @formatter:on */
    }

    @Test
    void usePressableConfig() {
        /*
         * When the mouse is told to use a not null value
         * for the pressable feature config, it is expected
         * to use the one specified.
         */
        PressableFeatureConfig config = new PressableFeatureConfig();
        mouse.usePressableConfig(config);
        assertSame(config, mouse.getPressableConfig());

        /*
         * When the mouse is told to use a null value for
         * the pressable feature config, it should use the
         * default configuration instead.
         */
        mouse.usePressableConfig(null);
        assertSame(PressableFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

    @Test
    void getPressableConfig() {
        /*
         * By default, mice should use the default pressable
         * feature config until a user specifies a different
         * one via usePressableConfig().
         */
        assertSame(PressableFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

}