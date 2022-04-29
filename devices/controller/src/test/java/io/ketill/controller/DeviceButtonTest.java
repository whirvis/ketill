package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class DeviceButtonTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new DeviceButton(null));
    }

    private DeviceButton button;

    @BeforeEach
    void setup() {
        this.button = new DeviceButton("button");
    }

    @Test
    void direction() {
        assertNull(button.direction);
    }

    @Test
    void getState() {
        ButtonStateZ internal = button.getInternalState();
        assertNotNull(internal);
        ButtonState container = button.getContainerState(internal);
        assertNotNull(container);
    }

}