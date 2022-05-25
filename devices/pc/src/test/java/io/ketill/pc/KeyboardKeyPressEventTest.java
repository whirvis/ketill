package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyboardKeyPressEventTest {

    private Keyboard keyboard;
    private KeyboardKey key;
    private KeyboardKeyPressEvent event;

    @BeforeEach
    void createEvent() {
        this.keyboard = mock(Keyboard.class);
        this.key = mock(KeyboardKey.class);
        this.event = new KeyboardKeyPressEvent(keyboard, key);
    }

    @Test
    void testGetKeyboard() {
        assertSame(keyboard, event.getKeyboard());
    }

    @Test
    void testGetKey() {
        assertSame(key, event.getKey());
    }

}
