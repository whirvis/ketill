package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyboardKeyTest {

    private KeyboardKey key;

    @BeforeEach
    void createKey() {
        this.key = new KeyboardKey("key");
    }

    @Test
    void testGetDevice() {
        assertSame(Keyboard.class, key.getDeviceType());
    }

    @Test
    void testGetState() {
        Keyboard keyboard = mock(Keyboard.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(keyboard);

        KeyPressZ internal = key.getInternalState(observer);
        assertNotNull(internal);

        KeyPress container = key.getContainerState(internal);
        assertNotNull(container);
    }

}
