package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryLevelTest {

    private BatteryLevelZ internal;
    private BatteryLevel container;

    @BeforeEach
    void createState() {
        this.internal = new BatteryLevelZ();
        this.container = new BatteryLevel(internal);
    }

    @Test
    void testGetLevel() {
        /*
         * At instantiation, the battery is level is unknown until the
         * adapter updates it manually. As such, the value should be
         * negative. A value of zero for the initial would indicate
         * that the battery is empty.
         */
        assertEquals(-1.0F, internal.level);

        internal.level = 0.123F;
        assertEquals(0.123F, container.getLevel());
    }

    @Test
    void testGetPercent() {
        /*
         * The getPercentage() method returns an integer on a scale from
         * 0 to 100. Since the returned value is rounded, the first test
         * should return a percentage of 12, and the next should return
         * a percentage of 13.
         */
        internal.level = 0.123F; /* too low to round */
        assertEquals(12, container.getPercent());
        internal.level = 0.125F; /* high enough to round */
        assertEquals(13, container.getPercent());

        /*
         * If the value returned by getLevel() returns -1.0F, then the
         * method getPercent() should return an integer value of -1.
         * It would not make sense to return a value of -100 to callers
         * who didn't know what was going on under the hood here.
         */
        internal.level = -1.0F;
        assertEquals(-1, container.getPercent());
    }

}
