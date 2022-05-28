package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.controller.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerObserverTest {

    private IoDeviceObserver deviceObserver;
    private TriggerStateZ internalState;
    private AnalogTriggerObserver triggerObserver;

    @BeforeEach
    void createObserver() {
        Controller controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        AnalogTrigger trigger = new AnalogTrigger("trigger");
        this.internalState = trigger.getInternalState(deviceObserver);

        this.triggerObserver = new AnalogTriggerObserver(trigger,
                internalState, deviceObserver);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressedImpl() {
        internalState.calibratedForce = 1.0F;
        assertTrue(triggerObserver.isPressedImpl());
        internalState.calibratedForce = 0.0F;
        assertFalse(triggerObserver.isPressedImpl());
    }

    @Test
    void testOnPress() {
        triggerObserver.onPress(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogTriggerPressEvent.class);
    }

    @Test
    void testOnHold() {
        triggerObserver.onHold(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogTriggerHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        triggerObserver.onRelease(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogTriggerReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {
        /*
         * Once the calibrated force of an analog trigger indicates that
         * it is pressed down, the trigger state should be updated by the
         * observer to indicate that it is pressed.
         */
        internalState.calibratedForce = 1.0F;
        triggerObserver.poll(); /* trigger state update */
        assertTrue(internalState.pressed);

        /*
         * After the calibrated force of an analog trigger has been pressed
         * for a long enough time to be considered held down, the trigger
         * state should be updated by the observer to indicate that it is
         * held down.
         */
        Thread.sleep(triggerObserver.getConfig().getHoldTime());
        triggerObserver.poll(); /* trigger state update */
        assertTrue(internalState.held);

        /*
         * Once the calibrated force of an analog trigger has been released
         * (after being pressed down), the trigger state should be updated
         * by the observer to indicate it is no longer pressed or held down.
         */
        internalState.calibratedForce = 0.0F;
        triggerObserver.poll(); /* trigger state update */
        assertFalse(internalState.pressed);
        assertFalse(internalState.held);
    }

}
