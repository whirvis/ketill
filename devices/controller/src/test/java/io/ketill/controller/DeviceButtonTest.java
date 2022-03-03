package io.ketill.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceButtonTest {

    @Test
    void __init__() {
        DeviceButton button = new DeviceButton("button");
        assertNull(button.direction);
    }

}
