package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseCursorSetIconEventTest {

    private Mouse mouse;
    private MouseCursor cursor;
    private Image icon;
    private MouseCursorSetIconEvent event;

    @BeforeEach
    void createEvent() {
        this.mouse = mock(Mouse.class);
        this.cursor = mock(MouseCursor.class);
        this.icon = mock(Image.class);
        this.event = new MouseCursorSetIconEvent(mouse, cursor, icon);
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
    void testGetIcon() {
        assertSame(icon, event.getIcon());
    }

}
