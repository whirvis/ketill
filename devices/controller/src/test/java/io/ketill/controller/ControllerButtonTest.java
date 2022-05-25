package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerButtonTest {

    private ControllerButton button;

    @BeforeEach
    void createButton() {
        this.button = new ControllerButton("button");
    }

    @Test
    void testInit() {
        /*
         * It is legal to create a controller button that does represent
         * a direction. As such, this should not throw an exception.
         */
        assertDoesNotThrow(() -> new ControllerButton("button", null));
    }

    @Test
    void testGetDirection() {
        assertNull(button.getDirection());
    }

    @Test
    void testGetState() {
        Controller controller = mock(Controller.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        ButtonStateZ internal = button.getInternalState(observer);
        assertNotNull(internal);

        ButtonState container = button.getContainerState(internal);
        assertNotNull(container);
    }

}
