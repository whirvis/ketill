package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerTest {

    private AnalogTrigger trigger;

    @BeforeEach
    void createTrigger() {
        this.trigger = new AnalogTrigger("trigger");
    }

    @Test
    void testIsPressed() {
        assertFalse(AnalogTrigger.isPressed(0.0F));
        assertTrue(AnalogTrigger.isPressed(1.0F));
    }

    @Test
    void testInit() {
        /*
         * It is legal to create an analog trigger without a base
         * calibration. As such, this should not throw an exception.
         */
        assertDoesNotThrow(() -> new AnalogTrigger("trigger", null));
    }

    @Test
    void testGetDeviceType() {
        assertSame(Controller.class, trigger.getDeviceType());
    }

    @Test
    void testGetBaseCalibration() {
        assertNull(trigger.getBaseCalibration());
    }

    @Test
    void testGetState() {
        Controller controller = mock(Controller.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        TriggerStateZ internal = trigger.getInternalState(observer);
        assertNotNull(internal);

        TriggerState container = trigger.getContainerState(internal);
        assertNotNull(container);
    }

}
