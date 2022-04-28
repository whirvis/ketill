package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogStickMonitorTest {

    private StickPosZ state;
    private AnalogStickMonitor monitor;

    @BeforeEach
    void setup() {
        Controller controller = mock(Controller.class);
        AnalogStick stick = new AnalogStick("stick");
        this.state = new StickPosZ();

        /*
         * These mock the functionality required for the pressable feature
         * monitor to instantiate without  any exceptions being thrown. At
         * construction, the monitor ensures the feature is registered and
         * that it owns the state supplied by the user.
         */
        when(controller.isFeatureRegistered(stick)).thenReturn(true);
        doReturn(stick).when(controller).getFeature(state);

        this.monitor = new AnalogStickMonitor(controller, stick, state,
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
         * When the PRESS event is fired, the analog stick monitor must set
         * the state of the button it was assigned to be considered pressed.
         */
        fireSimulatedEvent(PressableFeatureEventType.PRESS);
        assertTrue(state.up.pressed);

        /*
         * When the HOLD event is fired, the analog stick monitor must set
         * the state of the button it was assigned to be held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.up.held);

        /*
         * When the RELEASE event is fired, the analog stick monitor must set
         * the state of the button it was assigned to no longer be considered
         * pressed or held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.up.pressed);
        assertFalse(state.up.held);
    }

    @Test
    void getEventData() {
        /*
         * The event data of an analog stick monitor is the direction of the
         * analog stick being monitored. This allows for the event listener
         * to know which direction an analog stick has been pressed toward.
         * Otherwise, this event is useless.
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