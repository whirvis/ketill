package io.ketill.pc;

import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseButtonMonitorTest {

    private MouseClickZ state;
    private MouseButtonMonitor monitor;

    @BeforeEach
    void createMonitor() {
        Mouse mouse = mock(Mouse.class);
        MouseButton button = new MouseButton("button");
        this.state = new MouseClickZ();

        /*
         * These mock the functionality required for the pressable feature
         * monitor to instantiate without any exceptions being thrown. At
         * construction, the monitor ensures the feature is registered and
         * that it owns the state supplied by the user.
         */
        doReturn(true).when(mouse).isFeatureRegistered(button);
        doReturn(button).when(mouse).getFeature(state);

        this.monitor = new MouseButtonMonitor(mouse, button, state,
                () -> null);
    }

    private void fireSimulatedEvent(PressableFeatureEventType type) {
        PressableFeatureEvent event = new PressableFeatureEvent(type,
                monitor.device, monitor.feature, false, null);
        monitor.eventFired(event);
    }

    @Test
    void testEvents() {
        /*
         * When the HOLD event is fired, the mouse button monitor must
         * set the state of the button it was assigned to be held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.held);

        /*
         * When the RELEASE event is fired, the mouse button monitor must
         * set the state of the button it was assigned to not be held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.held);
    }

    /*
     * For some reason, IntelliJ says that the code below has a redundant
     * assignment. Furthermore, it says monitor.isPressed() is simplifiable
     * to true. These warnings are both wrong (IDE bug perhaps?). As such,
     * I had them suppressed.
     */
    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressed() {
        state.pressed = true;
        assertTrue(monitor.isPressed());
        state.pressed = false;
        assertFalse(monitor.isPressed());
    }

}
