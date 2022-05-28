package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseButtonPressEventTest {

    private Mouse mouse;
    private MouseButton button;
    private MouseButtonPressEvent event;

    @BeforeEach
    void createEvent() {
        this.mouse = mock(Mouse.class);
        this.button = mock(MouseButton.class);
        this.event = new MouseButtonPressEvent(mouse, button);
    }

    @Test
    void testGetMouse() {
        assertSame(mouse, event.getMouse());
    }

    @Test
    void testGetButton() {
        assertSame(button, event.getButton());
    }

}
