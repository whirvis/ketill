package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogStickObserverTest {

    private Controller controller;
    private IoDeviceObserver deviceObserver;
    private AnalogStick stick;
    private StickPosZ internalState;
    private ButtonStateZ buttonState;
    private Direction direction;
    private AnalogStickObserver stickObserver;

    private void verifyEmittedEvent(Class<?> type) {
        verify(deviceObserver).onNext(argThat(matcher -> {
            AnalogStickEvent event = (AnalogStickEvent) matcher;
            return event.getController() == controller
                    && event.getStick() == stick
                    && event.getDirection() == direction
                    && event.getClass().isAssignableFrom(type);
        }));
    }

    @BeforeEach
    void createObserver() {
        this.controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        this.stick = new AnalogStick("stick");
        this.internalState = stick.getInternalState(deviceObserver);
        this.buttonState = internalState.up;
        this.direction = Direction.UP;

        this.stickObserver = new AnalogStickObserver(stick, direction,
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
        verifyEmittedEvent(AnalogStickPressEvent.class);
    }

    @Test
    void testOnHold() {
        stickObserver.onHold(); /* trigger event emission */
        verifyEmittedEvent(AnalogStickHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        stickObserver.onRelease(); /* trigger event emission */
        verifyEmittedEvent(AnalogStickReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {

        internalState.calibratedPos.y = 1.0F;
        stickObserver.poll();
        assertTrue(buttonState.pressed);

        Thread.sleep(stickObserver.getConfig().getHoldTime());
        stickObserver.poll();
        assertTrue(buttonState.held);

        internalState.calibratedPos.y = 0.0F;
        stickObserver.poll();
        assertFalse(buttonState.pressed);
        assertFalse(buttonState.held);
    }

}
