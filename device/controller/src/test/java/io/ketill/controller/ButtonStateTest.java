package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.controller.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ButtonStateTest {

    private IoDeviceObserver observer;
    private ButtonStateZ internal;
    private ButtonState container;

    @BeforeEach
    void createState() {
        Controller controller = mock(Controller.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        ControllerButton button = new ControllerButton("button");
        this.internal = new ButtonStateZ(button, observer);
        this.container = new ButtonState(internal);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsPressed() {
        internal.pressed = true;
        assertTrue(container.isPressed());
        internal.pressed = false;
        assertFalse(container.isPressed());
    }

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    @Test
    void testIsHeld() {
        internal.held = true;
        assertTrue(container.isHeld());
        internal.held = false;
        assertFalse(container.isHeld());
    }

    @Test
    void testUpdate() {
        internal.pressed = true; /* press button */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, ControllerButtonPressEvent.class);

        internal.pressed = false; /* release button */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, ControllerButtonReleaseEvent.class);
    }

}
