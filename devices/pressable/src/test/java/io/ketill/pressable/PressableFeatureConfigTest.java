package io.ketill.pressable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PressableFeatureConfigTest {

    private PressableFeatureConfig config;

    @BeforeEach
    void setup() {
        this.config = new PressableFeatureConfig();
    }

    @Test
    void getHoldTime() {
        /*
         * Since this configuration was created with the default constructor,
         * the hold time should be equal to the default value.
         */
        assertEquals(PressableFeatureConfig.DEFAULT_HOLD_TIME,
                config.getHoldTime());
    }

    @Test
    void setHoldTime() {
        long holdTime = new Random().nextLong();
        config.setHoldTime(holdTime);
        assertEquals(holdTime, config.getHoldTime());
    }

    @Test
    void isHoldEnabled() {
        /*
         * Since this configuration was created with the default constructor,
         * feature holding should be enabled already.
         */
        assertTrue(config.isHoldEnabled());

        /*
         * After setting the hold time to the provided disable value, the
         * configuration should report that feature holding is disabled.
         */
        config.setHoldTime(PressableFeatureConfig.DISABLE_HOLD);
        assertFalse(config.isHoldEnabled());
    }

    @Test
    void getHoldPressInterval() {
        /*
         * Since this configuration was created with the default constructor,
         *  the hold press interval should be the default value.
         */
        assertEquals(PressableFeatureConfig.DEFAULT_HOLD_PRESS_INTERVAL,
                config.getHoldPressInterval());
    }

    @Test
    void setHoldPressInterval() {
        long holdPressInterval = new Random().nextLong();
        config.setHoldPressInterval(holdPressInterval);
        assertEquals(holdPressInterval, config.getHoldPressInterval());
    }

    @Test
    void isHoldPressEnabled() {
        /*
         * Since this configuration was created with the default constructor,
         * virtual feature pressing should be enabled.
         */
        assertTrue(config.isHoldPressEnabled());

        /*
         * After setting the hold press interval to the provided disable
         * value, the configuration should report that virtual feature
         * pressing is disabled. Otherwise, it means either the method
         * is broken or the value is incorrect.
         */
        config.setHoldPressInterval(PressableFeatureConfig.DISABLE_HOLD_PRESS);
        assertFalse(config.isHoldPressEnabled());
    }

    @Test
    void valueOf() {
        /*
         * When the provided configuration is not null, the valueOf()
         * method must return the given value.
         */
        assertSame(config, PressableFeatureConfig.valueOf(config));

        /*
         * When the provided configuration is indeed null, the valueOf()
         * method must return the default config instance.
         */
        assertSame(PressableFeatureConfig.DEFAULT,
                PressableFeatureConfig.valueOf(null));
    }

}