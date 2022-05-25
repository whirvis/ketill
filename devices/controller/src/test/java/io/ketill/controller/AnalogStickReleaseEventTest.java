package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AnalogStickReleaseEventTest {

    private Controller controller;
    private AnalogStick stick;
    private Direction direction;
    private AnalogStickReleaseEvent event;

    @BeforeEach
    void createEvent() {
        this.controller = mock(Controller.class);
        this.stick = mock(AnalogStick.class);
        this.direction = Direction.UP;
        this.event = new AnalogStickReleaseEvent(controller, stick, direction);
    }

    @Test
    void testInit() {
        /*
         * Analog stick events extend from pressable events. In this context,
         * the directions of an analog stick are what is pressable. As such,
         * any event occurring in a null direction would not make sense.
         */
        assertThrows(NullPointerException.class,
                () -> new AnalogStickReleaseEvent(controller, stick, null));
    }

    @Test
    void testGetController() {
        assertSame(controller, event.getController());
    }

    @Test
    void testGetStick() {
        assertSame(stick, event.getStick());
    }

    @Test
    void testGetDirection() {
        assertSame(direction, event.getDirection());
    }

}
