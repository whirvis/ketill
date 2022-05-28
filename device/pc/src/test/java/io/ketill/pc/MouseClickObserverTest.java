package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.pc.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseClickObserverTest {

    private IoDeviceObserver deviceObserver;
    private MouseClickZ internalState;
    private MouseClickObserver buttonObserver;

    @BeforeEach
    void createObserver() {
        Mouse mouse = mock(Mouse.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(mouse);

        MouseButton button = new MouseButton("button");
        this.internalState = button.getInternalState(deviceObserver);

        this.buttonObserver = new MouseClickObserver(button, internalState,
                deviceObserver);
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
        assertEmitted(deviceObserver, MouseButtonPressEvent.class);
    }

    @Test
    void testOnHold() {
        buttonObserver.onHold(); /* trigger event emission */
        assertEmitted(deviceObserver, MouseButtonHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        buttonObserver.onRelease(); /* trigger event emission */
        assertEmitted(deviceObserver, MouseButtonReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {
        /* press mouse button for next test */
        internalState.pressed = true;
        buttonObserver.poll(); /* observer press */

        /*
         * After a mouse button has been pressed for a long enough time
         * to be considered held down, the button state should be updated
         * by the observer to indicate that it is held down.
         */
        Thread.sleep(buttonObserver.getConfig().getHoldTime());
        buttonObserver.poll(); /* trigger state update */
        assertTrue(internalState.held);

        /*
         * Once a mouse button has been released (after being pressed down),
         * the button state should be updated by the observer to indicate
         * that it is also no longer held down.
         */
        internalState.pressed = false;
        buttonObserver.poll(); /* trigger state update */
        assertFalse(internalState.held);
    }

}
