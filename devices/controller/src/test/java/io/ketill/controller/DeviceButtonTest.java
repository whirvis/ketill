package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class DeviceButtonTest {

    private DeviceButton button;

    @BeforeEach
    void createButton() {
        this.button = new DeviceButton("button");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new DeviceButton(null));
    }

    @Test
    void ensureNullDirection() {
        assertNull(button.direction);
    }

    @Test
    void testGetState() {
        ButtonStateZ internal = button.getInternalState();
        assertNotNull(internal);
        ButtonState container = button.getContainerState(internal);
        assertNotNull(container);
    }

}