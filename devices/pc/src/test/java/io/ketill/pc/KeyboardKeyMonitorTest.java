package io.ketill.pc;

import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyboardKeyMonitorTest {

    private Key1b state;
    private KeyboardKeyMonitor monitor;

    @BeforeEach
    void setup() {
        Keyboard keyboard = mock(Keyboard.class);
        KeyboardKey key = new KeyboardKey("key");
        this.state = new Key1b();

        /*
         * These mock the functionality required for the
         * pressable feature monitor to instantiate without
         * any exceptions being thrown. At construction,
         * the monitor ensures the feature is registered
         * and fetches the feature's state from the device.
         */
        when(keyboard.isFeatureRegistered(key)).thenReturn(true);
        when(keyboard.getState(key)).thenReturn(state);

        this.monitor = new KeyboardKeyMonitor(keyboard, key, () -> null);
    }

    private void fireSimulatedEvent(PressableFeatureEventType type) {
        PressableFeatureEvent event = new PressableFeatureEvent(type,
                monitor.device, monitor.feature, false, null);
        monitor.eventFired(event);
    }

    @Test
    void eventFired() {
        /*
         * When the HOLD event is fired, the keyboard key
         * monitor must set the state of the key it has
         * been assigned to be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.HOLD);
        assertTrue(state.isHeld());

        /*
         * When the RELEASE event is fired, the keyboard key
         * monitor must set the state of the key it has been
         * assigned to no longer be considered held down.
         */
        fireSimulatedEvent(PressableFeatureEventType.RELEASE);
        assertFalse(state.isHeld());
    }

    @Test
    void isPressed() {
        state.pressed = true;
        assertTrue(monitor.isPressed());
        state.pressed = false;
        assertFalse(monitor.isPressed());
    }

}
