package io.ketill.pc;

import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.ketill.KetillAssertions.*;
import static io.ketill.pc.Mouse.*;
import static org.junit.jupiter.api.Assertions.*;

class MouseTest {

    private Mouse mouse;

    @BeforeEach
    void setup() {
        this.mouse = new Mouse(MockPcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(mouse, mouse.m1, BUTTON_M1);
        assertFeatureOwnsState(mouse, mouse.m2, BUTTON_M2);
        assertFeatureOwnsState(mouse, mouse.m3, BUTTON_M3);
        assertFeatureOwnsState(mouse, mouse.m4, BUTTON_M4);
        assertFeatureOwnsState(mouse, mouse.m5, BUTTON_M5);
        assertFeatureOwnsState(mouse, mouse.m6, BUTTON_M6);
        assertFeatureOwnsState(mouse, mouse.m7, BUTTON_M7);
        assertFeatureOwnsState(mouse, mouse.m8, BUTTON_M8);

        assertSame(mouse.left, mouse.m1);
        assertSame(mouse.right, mouse.m2);
        assertSame(mouse.middle, mouse.m3);

        assertFeatureOwnsState(mouse, mouse.cursor, FEATURE_CURSOR);
    }

    @Test
    void onMouseButtonEvent() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        mouse.onPressableEvent(e -> notified.set(true));

        /*
         * When a mouse button is registered, the mouse should notify
         * listeners when it is pressed or released.
         */
        MouseButton button = new MouseButton("button");
        mouse.registerFeature(button);
        MouseClickZ state = mouse.getInternalState(button);

        state.pressed = true; /* click button */
        mouse.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When a mouse button is unregistered, the mouse should no
         * longer notify listeners when it is pressed or released.
         */
        mouse.unregisterFeature(button);
        notified.set(false);

        state.pressed = false; /* unclick button */
        mouse.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void getPressableCallback() {
        assertNull(mouse.getPressableCallback());
    }

    @Test
    void usePressableCallback() {
        /* @formatter:off */
        Consumer<PressableFeatureEvent> callback = (e) -> {};
        mouse.onPressableEvent(callback);
        assertSame(callback, mouse.getPressableCallback());
        /* @formatter:on */
    }

    @Test
    void usePressableConfig() {
        PressableFeatureConfig config = new PressableFeatureConfig();
        mouse.usePressableConfig(config);
        assertSame(config, mouse.getPressableConfig());

        /*
         * When the mouse is told to use a null value for the pressable
         * feature config, it should use the default configuration instead.
         */
        mouse.usePressableConfig(null);
        assertSame(PressableFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

    @Test
    void getPressableConfig() {
        assertSame(PressableFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

}
