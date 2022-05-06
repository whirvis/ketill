package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class MouseButtonTest {

    private MouseButton key;

    @BeforeEach
    void createButton() {
        this.key = new MouseButton("button");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new MouseButton(null));
    }

    @Test
    void testGetState() {
        MouseClickZ internal = key.getInternalState();
        assertNotNull(internal);
        MouseClick container = key.getContainerState(internal);
        assertNotNull(container);
    }

}
