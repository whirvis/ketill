package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseCursorSetVisibilityEventTest {

    private Mouse mouse;
    private MouseCursor cursor;
    private MouseCursorSetVisibilityEvent event;

    @BeforeEach
    void createEvent() {
        this.mouse = mock(Mouse.class);
        this.cursor = mock(MouseCursor.class);
        this.event = new MouseCursorSetVisibilityEvent(mouse, cursor, true);
    }

    @Test
    void testGetMouse() {
        assertSame(mouse, event.getMouse());
    }

    @Test
    void testGetCursor() {
        assertSame(cursor, event.getCursor());
    }

    @Test
    void testIsVisible() {
        assertTrue(event.isVisible());
    }

}
