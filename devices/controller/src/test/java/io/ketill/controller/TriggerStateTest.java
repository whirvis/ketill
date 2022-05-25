package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.*;

class TriggerStateTest {

    private AnalogTrigger trigger;
    private AnalogTriggerCalibration calibration;
    private Controller controller;
    private IoDeviceObserver observer;
    private TriggerStateZ internal;
    private TriggerState container;

    private void verifyEmittedEvent(Class<?> type) {
        verify(observer).onNext(argThat(matcher -> {
            AnalogTriggerEvent event = (AnalogTriggerEvent) matcher;
            return event.getController() == controller
                    && event.getTrigger() == trigger
                    && event.getClass().isAssignableFrom(type);
        }));
    }

    @BeforeEach
    void createState() {
        this.trigger = new AnalogTrigger("trigger");
        this.calibration = mock(AnalogTriggerCalibration.class);
        when(calibration.apply(anyFloat())).thenAnswer(answer -> answer.getArgument(0, Float.class));

        this.controller = mock(Controller.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        this.internal = new TriggerStateZ(trigger, observer, calibration);
        this.container = new TriggerState(internal);
    }

    @Test
    void testUseCalibration() {
        container.useCalibration(null);
        assertNull(container.getCalibration());
        container.useCalibration(calibration);
        assertSame(calibration, container.getCalibration());
    }

    @Test
    void testGetCalibration() {
        assertSame(calibration, container.getCalibration());
    }

    @Test
    void testGetForce() {
        internal.force = 1.23F;
        assertEquals(1.23F, container.getForce(false));

        internal.calibratedForce = 4.56F;
        assertEquals(4.56F, container.getForce());
    }

    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @Test
    void testIsHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

    @Test
    void testUpdate() {
        internal.update(); /* trigger calibrations */
        verify(calibration).apply(anyFloat());

        internal.force = 1.0F; /* press trigger */
        internal.update(); /* trigger event emission */
        verifyEmittedEvent(AnalogTriggerPressEvent.class);

        internal.force = 0.0F; /* release trigger */
        internal.update(); /* trigger event emission */
        verifyEmittedEvent(AnalogTriggerReleaseEvent.class);
    }

}
