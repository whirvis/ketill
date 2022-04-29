package io.ketill.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class RumbleMotorTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new RumbleMotor(null));
    }

}
