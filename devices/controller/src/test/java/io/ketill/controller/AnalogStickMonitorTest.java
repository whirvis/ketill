package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogStickMonitorTest {

    private Stick3f state;
    private AnalogStickMonitor monitor;

    @BeforeEach
    void setup() {
        Controller controller = mock(Controller.class);
        AnalogStick stick = new AnalogStick("stick");
        this.state = new Stick3f();

        /*
         * These mock the functionality required for the
         * pressable feature monitor to instantiate without
         * any exceptions being thrown. At construction,
         * the monitor ensures the feature is registered
         * and fetches the feature's state from the device.
         */
        when(controller.isFeatureRegistered(stick)).thenReturn(true);
        when(controller.getState(stick)).thenReturn(state);

        this.monitor = new AnalogStickMonitor(controller, stick,
                Direction.UP, state.up, () -> null);
    }

    private void fireSimulatedEvent(PressableFeatureEventType type) {
        Object eventData = monitor.getEventData();
        PressableFeatureEvent event = new PressableFeatureEvent(type,
                monitor.device, monitor.feature, false, eventData);
        monitor.eventFired(event);
    }

    @Test
    void eventFired() {
        /*
         * When the PRESS event is fired, the analog stick
         * monitor must set the state of the button it has
         * been assigned to be considered pressed.
         */
        fireSimulatedEvent(PressableFeatureEventType.PRESS);
        assertTrue(state.up().isPressed());

        /*
         * When the HOLD event is fired, the analog stick
         * monitor must set the state of the button it has
         * been assigned to be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.up().isHeld());

        /*
         * When the RELEASE event is fired, the analog stick
         * monitor must set the state of the button it has
         * been assigned to no longer be considered pressed
         * or held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.up().isPressed());
        assertFalse(state.up().isHeld());
    }

    @Test
    void getEventData() {
        /*
         * The event data of an analog stick monitor is the
         * direction of the analog stick being monitored.
         * This is required for the event listener to know
         * which direction an analog stick has been pressed
         * toward. Otherwise, the event is useless.
         */
        assertSame(Direction.UP, monitor.getEventData());
    }

    @Test
    void isPressed() {
        state.y = 1.0F; /* facing upwards */
        assertTrue(monitor.isPressed());
        state.y = 0.0F; /* facing center */
        assertFalse(monitor.isPressed());
    }

}
