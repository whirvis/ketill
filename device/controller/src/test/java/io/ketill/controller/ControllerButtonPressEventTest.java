package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerButtonPressEventTest {

    private Controller controller;
    private ControllerButton button;
    private ControllerButtonPressEvent event;

    @BeforeEach
    void createEvent() {
        this.controller = mock(Controller.class);
        this.button = mock(ControllerButton.class);
        this.event = new ControllerButtonPressEvent(controller, button);
    }

    @Test
    void testGetController() {
        assertSame(controller, event.getController());
    }

    @Test
    void testGetButton() {
        assertSame(button, event.getButton());
    }

}
