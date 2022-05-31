package io.ketill.pc;

import io.ketill.pressable.PressableIoFeatureConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.pc.Mouse.*;
import static org.junit.jupiter.api.Assertions.*;

class MouseTest {

    private Mouse mouse;

    @BeforeEach
    void createMouse() {
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
    void testUsePressableConfig() {
        PressableIoFeatureConfig config = new PressableIoFeatureConfig();
        mouse.usePressableConfig(config);
        assertSame(config, mouse.getPressableConfig());

        /*
         * When the mouse is told to use a null value for the pressable
         * feature config, it should use the default configuration instead.
         */
        mouse.usePressableConfig(null);
        assertSame(PressableIoFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

    @Test
    void testGetPressableConfig() {
        assertSame(PressableIoFeatureConfig.DEFAULT,
                mouse.getPressableConfig());
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(Mouse.class, mouse);
    }

}
