package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerButtonReleaseEventTest {

    private Controller controller;
    private ControllerButton button;
    private ControllerButtonReleaseEvent event;

    @BeforeEach
    void createEvent() {
        this.controller = mock(Controller.class);
        this.button = mock(ControllerButton.class);
        this.event = new ControllerButtonReleaseEvent(controller, button);
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
