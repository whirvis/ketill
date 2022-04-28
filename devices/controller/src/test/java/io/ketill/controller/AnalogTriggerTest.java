package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class AnalogTriggerTest {

    @Test
    void isPressed() {
        assertFalse(AnalogTrigger.isPressed(0.0F));
        assertTrue(AnalogTrigger.isPressed(1.0F));
    }

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new AnalogTrigger(null));
    }

    private AnalogTrigger trigger;

    @BeforeEach
    void setup() {
        this.trigger = new AnalogTrigger("trigger");
    }

    @Test
    void getState() {
        TriggerStateZ internal = trigger.getInternalState();
        assertNotNull(internal);
        TriggerState container = trigger.getContainerState(internal);
        assertNotNull(container);
    }

}
