package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseCursorTest {

    private MouseCursor cursor;

    @BeforeEach
    void createCursor() {
        this.cursor = new MouseCursor("cursor");
    }

    @Test
    void testGetDevice() {
        assertSame(Mouse.class, cursor.getDeviceType());
    }

    @Test
    void testGetState() {
        Mouse mouse = mock(Mouse.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(mouse);

        CursorStateZ internal = cursor.getInternalState(observer);
        assertNotNull(internal);

        CursorState container = cursor.getContainerState(internal);
        assertNotNull(container);
    }

}
