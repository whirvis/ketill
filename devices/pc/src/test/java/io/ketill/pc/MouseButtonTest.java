package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class MouseButtonTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new MouseButton(null));
    }

    private MouseButton key;

    @BeforeEach
    void setup() {
        this.key = new MouseButton("button");
    }

    @Test
    void getState() {
        MouseClickZ internal = key.getInternalState();
        assertNotNull(internal);
        MouseClick container = key.getContainerState(internal);
        assertNotNull(container);
    }

}
