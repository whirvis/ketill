package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseButtonTest {

    private MouseButton button;

    @BeforeEach
    void createButton() {
        this.button = new MouseButton("button");
    }

    @Test
    void testGetDevice() {
        assertSame(Mouse.class, button.getDeviceType());
    }

    @Test
    void testGetState() {
        Mouse mouse = mock(Mouse.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(mouse);

        MouseClickZ internal = button.getInternalState(observer);
        assertNotNull(internal);

        MouseClick container = button.getContainerState(internal);
        assertNotNull(container);
    }

}
