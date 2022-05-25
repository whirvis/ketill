package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerButtonObserverTest {

    private Controller controller;
    private IoDeviceObserver deviceObserver;
    private ControllerButton button;
    private ButtonStateZ internalState;
    private ControllerButtonObserver buttonObserver;

    private void verifyEmittedEvent(Class<?> type) {
        verify(deviceObserver).onNext(argThat(matcher -> {
            ControllerButtonEvent event = (ControllerButtonEvent) matcher;
            return event.getController() == controller
                    && event.getButton() == button
                    && event.getClass().isAssignableFrom(type);
        }));
    }

    @BeforeEach
    void createObserver() {
        this.controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        this.button = new ControllerButton("button");
        this.internalState = button.getInternalState(deviceObserver);

        this.buttonObserver = new ControllerButtonObserver(button,
                internalState, deviceObserver);
    }

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
        verifyEmittedEvent(ControllerButtonPressEvent.class);
    }

    @Test
    void testOnHold() {
        buttonObserver.onHold(); /* trigger event emission */
        verifyEmittedEvent(ControllerButtonHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        buttonObserver.onRelease(); /* trigger event emission */
        verifyEmittedEvent(ControllerButtonReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {

        internalState.pressed = true;
        buttonObserver.poll();

        Thread.sleep(buttonObserver.getConfig().getHoldTime());
        buttonObserver.poll();
        assertTrue(internalState.held);

        internalState.pressed = false;
        buttonObserver.poll();
        assertFalse(internalState.held);
    }

}
