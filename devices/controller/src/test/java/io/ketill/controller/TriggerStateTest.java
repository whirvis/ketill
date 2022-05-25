package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.controller.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TriggerStateTest {

    private IoDeviceObserver observer;
    private AnalogTriggerCalibration calibration;
    private TriggerStateZ internal;
    private TriggerState container;

    @BeforeEach
    void createState() {
        AnalogTrigger trigger = new AnalogTrigger("trigger");
        this.calibration = mock(AnalogTriggerCalibration.class);
        when(calibration.apply(anyFloat())).thenAnswer(answer -> answer.getArgument(0, Float.class));

        Controller controller = mock(Controller.class);
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

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
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
        assertEmitted(observer, AnalogTriggerPressEvent.class);

        internal.force = 0.0F; /* release trigger */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, AnalogTriggerReleaseEvent.class);
    }

}
