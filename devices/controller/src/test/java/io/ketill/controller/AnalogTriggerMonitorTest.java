package io.ketill.controller;

import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerMonitorTest {

    private Trigger1f state;
    private AnalogTriggerMonitor monitor;

    @BeforeEach
    void setup() {
        Controller controller = mock(Controller.class);
        AnalogTrigger trigger = new AnalogTrigger("trigger");
        this.state = new Trigger1f();

        /*
         * These mock the functionality required for the
         * pressable feature monitor to instantiate without
         * any exceptions being thrown. At construction,
         * the monitor ensures the feature is registered
         * and fetches the feature's state from the device.
         */
        when(controller.isFeatureRegistered(trigger)).thenReturn(true);
        when(controller.getState(trigger)).thenReturn(state);

        this.monitor = new AnalogTriggerMonitor(controller, trigger,
                () -> null);
    }

    private void fireSimulatedEvent(PressableFeatureEventType type) {
        PressableFeatureEvent event = new PressableFeatureEvent(type,
                monitor.device, monitor.feature, false, null);
        monitor.eventFired(event);
    }

    @Test
    void eventFired() {
        /*
         * When the PRESS event is fired, the analog trigger
         * monitor must set the state of the trigger's button
         * representation to be considered pressed.
         */
        fireSimulatedEvent(PressableFeatureEventType.PRESS);
        assertTrue(state.isPressed());

        /*
         * When the HOLD event is fired, the analog trigger
         * monitor must set the state of the trigger's button
         * representation to be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.isHeld());

        /*
         * When the RELEASE event is fired, the analog trigger
         * monitor must set the state of the trigger's button
         * representation to no longer be considered pressed
         * or held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.isPressed());
        assertFalse(state.isHeld());
    }

    @Test
    void isPressed() {
        state.force = 1.0F; /* fully pressed */
        assertTrue(monitor.isPressed());
        state.force = 0.0F; /* fully released */
        assertFalse(monitor.isPressed());
    }

}
