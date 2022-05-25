package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.controller.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerButtonObserverTest {

    private IoDeviceObserver deviceObserver;
    private ButtonStateZ internalState;
    private ControllerButtonObserver buttonObserver;

    @BeforeEach
    void createObserver() {
        Controller controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        ControllerButton button = new ControllerButton("button");
        this.internalState = button.getInternalState(deviceObserver);

        this.buttonObserver = new ControllerButtonObserver(button,
                internalState, deviceObserver);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressedImpl() {
        internalState.pressed = true;
        assertTrue(buttonObserver.isPressedImpl());
        internalState.pressed = false;
        assertFalse(buttonObserver.isPressedImpl());
    }

    @Test
    void testOnPress() {
        buttonObserver.onPress(); /* trigger event emission */
        assertEmitted(deviceObserver, ControllerButtonPressEvent.class);
    }

    @Test
    void testOnHold() {
        buttonObserver.onHold(); /* trigger event emission */
        assertEmitted(deviceObserver, ControllerButtonHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        buttonObserver.onRelease(); /* trigger event emission */
        assertEmitted(deviceObserver, ControllerButtonReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {
        /* press button for next test */
        internalState.pressed = true;
        buttonObserver.poll(); /* observe press */

        /*
         * After a button has been pressed for a long enough time to be
         * considered held down, the button state should be updated by
         * the observer to indicate that it is held down.
         */
        Thread.sleep(buttonObserver.getConfig().getHoldTime());
        buttonObserver.poll(); /* trigger state update */
        assertTrue(internalState.held);

        /*
         * Once a button has been released (after being pressed down), the
         * button state should be updated by the observer to indicate that
         * it is also no longer held down.
         */
        internalState.pressed = false;
        buttonObserver.poll(); /* trigger state update */
        assertFalse(internalState.held);
    }

}
