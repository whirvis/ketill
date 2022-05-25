package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RumbleMotorTest {

    private RumbleMotor motor;

    @BeforeEach
    void createMotor() {
        this.motor = new RumbleMotor("motor");
    }

    @Test
    void testGetDeviceType() {
        assertSame(Controller.class, motor.getDeviceType());
    }

}
