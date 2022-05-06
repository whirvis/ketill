package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class AnalogTriggerTest {

    @Test
    void testIsPressed() {
        assertFalse(AnalogTrigger.isPressed(0.0F));
        assertTrue(AnalogTrigger.isPressed(1.0F));
    }

    private AnalogTrigger trigger;

    @BeforeEach
    void createTrigger() {
        this.trigger = new AnalogTrigger("trigger");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new AnalogTrigger(null));
    }

    @Test
    void testGetState() {
        TriggerStateZ internal = trigger.getInternalState();
        assertNotNull(internal);
        TriggerState container = trigger.getContainerState(internal);
        assertNotNull(container);
    }

}
