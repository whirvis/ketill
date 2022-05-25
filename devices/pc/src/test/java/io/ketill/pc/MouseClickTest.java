package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.pc.EventAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseClickTest {

    private IoDeviceObserver observer;
    private MouseClickZ internal;
    private MouseClick container;

    @BeforeEach
    void createState() {
        Mouse mouse = mock(Mouse.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(mouse);

        MouseButton button = new MouseButton("button");
        this.internal = new MouseClickZ(button, observer);
        this.container = new MouseClick(internal);
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
        internal.pressed = true;
        internal.update(); /* trigger event emission */
        assertEmitted(observer, MouseButtonPressEvent.class);

        internal.pressed = false; /* release trigger */
        internal.update(); /* trigger event emission */
        assertEmitted(observer, MouseButtonReleaseEvent.class);
    }


}
