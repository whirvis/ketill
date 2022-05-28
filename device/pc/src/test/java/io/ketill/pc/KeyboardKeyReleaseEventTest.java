package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeyboardKeyReleaseEventTest {

    private Keyboard keyboard;
    private KeyboardKey key;
    private KeyboardKeyReleaseEvent event;

    @BeforeEach
    void createEvent() {
        this.keyboard = mock(Keyboard.class);
        this.key = mock(KeyboardKey.class);
        this.event = new KeyboardKeyReleaseEvent(keyboard, key);
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
