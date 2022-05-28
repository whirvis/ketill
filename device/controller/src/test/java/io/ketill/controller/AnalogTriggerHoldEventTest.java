package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalogTriggerHoldEventTest {

    private Controller controller;
    private AnalogTrigger trigger;
    private AnalogTriggerHoldEvent event;

    @BeforeEach
    void createEvent() {
        this.controller = mock(Controller.class);
        this.trigger = mock(AnalogTrigger.class);
        this.event = new AnalogTriggerHoldEvent(controller, trigger);
    }

    @Test
    void testGetController() {
        assertSame(controller, event.getController());
    }

    @Test
    void testGetTrigger() {
        assertSame(trigger, event.getTrigger());
    }

}
