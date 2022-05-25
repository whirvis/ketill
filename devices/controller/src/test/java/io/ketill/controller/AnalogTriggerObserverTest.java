package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerObserverTest {

    private Controller controller;
    private IoDeviceObserver deviceObserver;
    private AnalogTrigger trigger;
    private TriggerStateZ internalState;
    private AnalogTriggerObserver triggerObserver;

    private void verifyEmittedEvent(Class<?> type) {
        verify(deviceObserver).onNext(argThat(matcher -> {
            AnalogTriggerEvent event = (AnalogTriggerEvent) matcher;
            return event.getController() == controller
                    && event.getTrigger() == trigger
                    && event.getClass().isAssignableFrom(type);
        }));
    }

    @BeforeEach
    void createObserver() {
        this.controller = mock(Controller.class);
        this.deviceObserver = mock(IoDeviceObserver.class);
        when(deviceObserver.getDevice()).thenReturn(controller);

        this.trigger = new AnalogTrigger("trigger");
        this.internalState = trigger.getInternalState(deviceObserver);

        this.triggerObserver = new AnalogTriggerObserver(trigger,
                internalState, deviceObserver);
    }

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
        verifyEmittedEvent(AnalogTriggerPressEvent.class);
    }

    @Test
    void testOnHold() {
        triggerObserver.onHold(); /* trigger event emission */
        verifyEmittedEvent(AnalogTriggerHoldEvent.class);
    }

    @Test
    void testOnRelease() {
        triggerObserver.onRelease(); /* trigger event emission */
        verifyEmittedEvent(AnalogTriggerReleaseEvent.class);
    }

    @Test
    void testPoll() throws InterruptedException {

        internalState.calibratedForce = 1.0F;
        triggerObserver.poll();
        assertTrue(internalState.pressed);

        Thread.sleep(triggerObserver.getConfig().getHoldTime());
        triggerObserver.poll();
        assertTrue(internalState.held);

        internalState.calibratedForce = 0.0F;
        triggerObserver.poll();
        assertFalse(internalState.pressed);
        assertFalse(internalState.held);
    }

}
