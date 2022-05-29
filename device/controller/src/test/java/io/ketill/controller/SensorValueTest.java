package io.ketill.controller;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorValueTest {

    private SensorValueZ internal;
    private SensorValue container;

    @BeforeEach
    void createState() {
        this.internal = new SensorValueZ();
        this.container = new SensorValue(internal);
    }

    @Test
    void testGetValue() {
        Vector3f value = new Vector3f(1.23F, 4.56F, 7.89F);
        internal.value.set(value);
        assertEquals(value, container.getValue());
    }

    @Test
    void testGetX() {
        internal.value.x = 1.23F;
        assertEquals(1.23F, container.getX());
    }

    @Test
    void testGetY() {
        internal.value.y = 1.23F;
        assertEquals(1.23F, container.getY());
    }

    @Test
    void testGetZ() {
        internal.value.z = 1.23F;
        assertEquals(1.23F, container.getZ());
    }

}
