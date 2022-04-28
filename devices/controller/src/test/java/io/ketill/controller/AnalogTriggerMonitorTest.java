package io.ketill.controller;

import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerMonitorTest {

    private TriggerStateZ state;
    private AnalogTriggerMonitor monitor;

    @BeforeEach
    void setup() {
        Controller controller = mock(Controller.class);
        AnalogTrigger trigger = new AnalogTrigger("trigger");
        this.state = new TriggerStateZ();

        /*
         * These mock the functionality required for the pressable feature
         * monitor to instantiate without any exceptions being thrown. At
         * construction, the monitor ensures the feature is registered and
         * that it owns the state supplied by the user.
         */
        doReturn(true).when(controller).isFeatureRegistered(trigger);
        doReturn(trigger).when(controller).getFeature(state);

        this.monitor = new AnalogTriggerMonitor(controller, trigger, state,
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
         * When the PRESS event is fired, the analog trigger monitor must
         * set the state of the button it was assigned to be pressed.
         */
        fireSimulatedEvent(PressableFeatureEventType.PRESS);
        assertTrue(state.pressed);

        /*
         * When the HOLD event is fired, the analog trigger monitor must
         * set the state of the button it was assigned to be held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.held);

        /*
         * When the RELEASE event is fired, the analog trigger monitor must
         * set the state of the button it was assigned to not be considered
         * pressed or held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.pressed);
        assertFalse(state.held);
    }

    /*
     * For some reason, IntelliJ says that the code below has a redundant
     * assignment. Furthermore, it says monitor.isPressed() is simplifiable
     * to true. This seems to be a bug in the IDE. These warnings are both
     * wrong (IDE bug perhaps?). As such, I had them suppressed.
     */
    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void isPressed() {
        state.force = 1.0F; /* fully pressed */
        assertTrue(monitor.isPressed());
        state.force = 0.0F; /* fully released */
        assertFalse(monitor.isPressed());
    }

}
