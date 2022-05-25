package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.controller.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogStickObserverTest {

    private IoDeviceObserver deviceObserver;
    private StickPosZ internalState;
    private ButtonStateZ buttonState;
    private AnalogStickObserver stickObserver;

    @BeforeEach
    void createObserver() {
        Controller controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        AnalogStick stick = new AnalogStick("stick");
        this.internalState = stick.getInternalState(deviceObserver);
        this.buttonState = internalState.up;

        this.stickObserver = new AnalogStickObserver(stick, Direction.UP,
                internalState, buttonState, deviceObserver);
    }

    @Test
    void testIsPressedImpl() {
        internalState.calibratedPos.y = 1.0F;
        assertTrue(stickObserver.isPressedImpl());
        internalState.calibratedPos.y = 0.0F;
        assertFalse(stickObserver.isPressedImpl());
    }

    @Test
    void testOnPress() {
        stickObserver.onPress(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogStickPressEvent.class);
    }

    @Test
    void testOnHold() {
        stickObserver.onHold(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogStickHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        stickObserver.onRelease(); /* trigger event emission */
        assertEmitted(deviceObserver, AnalogStickReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {
        /*
         * Once the calibrated position of an analog stick is pressed towards
         * the observed direction, the button state should be updated by the
         * observer to indicate that it is pressed.
         */
        internalState.calibratedPos.y = 1.0F;
        stickObserver.poll(); /* trigger state update */
        assertTrue(buttonState.pressed);

        /*
         * After the calibrated position of an analog stick has been pressed
         * for a long enough time to be considered held down, the button
         * state should be updated by the observer to indicate that it is
         * held down.
         */
        Thread.sleep(stickObserver.getConfig().getHoldTime());
        stickObserver.poll(); /* trigger state update */
        assertTrue(buttonState.held);

        /*
         * Once the calibrated position of an analog stick has been released
         * (after being pressed down), the button state should be updated by
         * the observer to indicate it is no longer pressed or held down.
         */
        internalState.calibratedPos.y = 0.0F;
        stickObserver.poll(); /* trigger state update */
        assertFalse(buttonState.pressed);
        assertFalse(buttonState.held);
    }

}
