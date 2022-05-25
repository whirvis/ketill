package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ButtonStateTest {

    private ControllerButton button;
    private Controller controller;
    private IoDeviceObserver observer;
    private ButtonStateZ internal;
    private ButtonState container;

    private void verifyEmittedEvent(Class<?> type) {
        verify(observer).onNext(argThat(matcher -> {
            ControllerButtonEvent event = (ControllerButtonEvent) matcher;
            return event.getController() == controller
                    && event.getButton() == button
                    && event.getClass().isAssignableFrom(type);
        }));
    }

    @BeforeEach
    void createState() {
        this.button = new ControllerButton("button");

        this.controller = mock(Controller.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        this.internal = new ButtonStateZ(button, observer);
        this.container = new ButtonState(internal);
    }

    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @Test
    void testIsHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

    @Test
    void testUpdate() {
        internal.pressed = true;
        internal.update(); /* trigger event emission */
        verifyEmittedEvent(ControllerButtonPressEvent.class);

        internal.pressed = false; /* release trigger */
        internal.update(); /* trigger event emission */
        verifyEmittedEvent(ControllerButtonReleaseEvent.class);
    }

}
