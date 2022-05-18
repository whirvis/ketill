package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class KeyboardKeyTest {

    private KeyboardKey key;

    @BeforeEach
    void createKey() {
        this.key = new KeyboardKey("key");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new KeyboardKey(null));
    }

    @Test
    void testGetState() {
        KeyPressZ internal = key.getInternalState(null);
        assertNotNull(internal);
        KeyPress container = key.getContainerState(internal);
        assertNotNull(container);
    }

}
