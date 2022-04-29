package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class KeyboardKeyTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new KeyboardKey(null));
    }

    private KeyboardKey key;

    @BeforeEach
    void setup() {
        this.key = new KeyboardKey("key");
    }

    @Test
    void getState() {
        KeyPressZ internal = key.getInternalState();
        assertNotNull(internal);
        KeyPress container = key.getContainerState(internal);
        assertNotNull(container);
    }

}
