package io.ketill.pc;

import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class MouseCursorDisplaceEventTest {

    private Mouse mouse;
    private MouseCursor cursor;
    private Vector2f displacement;
    private MouseCursorDisplaceEvent event;

    @BeforeEach
    void createEvent() {
        this.mouse = mock(Mouse.class);
        this.cursor = mock(MouseCursor.class);
        this.displacement = new Vector2f();
        this.event = new MouseCursorDisplaceEvent(mouse, cursor, displacement);
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
    void testGetDisplacement() {
        assertSame(displacement, event.getDisplacement());
    }

}
