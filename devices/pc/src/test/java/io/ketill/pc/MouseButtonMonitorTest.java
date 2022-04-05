package io.ketill.pc;

import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseButtonMonitorTest {

    private Click1b state;
    private MouseButtonMonitor monitor;

    @BeforeEach
    void setup() {
        Mouse mouse = mock(Mouse.class);
        MouseButton button = new MouseButton("button");
        this.state = new Click1b();

        /*
         * These mock the functionality required for the
         * pressable feature monitor to instantiate without
         * any exceptions being thrown. At construction,
         * the monitor ensures the feature is registered
         * and fetches the feature's state from the device.
         */
        when(mouse.isFeatureRegistered(button)).thenReturn(true);
        when(mouse.getState(button)).thenReturn(state);

        this.monitor = new MouseButtonMonitor(mouse, button, () -> null);
    }

    private void fireSimulatedEvent(PressableFeatureEventType type) {
        PressableFeatureEvent event = new PressableFeatureEvent(type,
                monitor.device, monitor.feature, false, null);
        monitor.eventFired(event);
    }

    @Test
    void eventFired() {
        /*
         * When the HOLD event is fired, the mouse button
         * monitor must set the state of the button it has
         * been assigned to be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.isHeld());

        /*
         * When the RELEASE event is fired, the mouse button
         * monitor must set the state of the button it has
         * been assigned to no longer be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.isHeld());
    }

    @Test
    void isPressed() {
        state.clicked = true;
        assertTrue(monitor.isPressed());
        state.clicked = false;
        assertFalse(monitor.isPressed());
    }

}
