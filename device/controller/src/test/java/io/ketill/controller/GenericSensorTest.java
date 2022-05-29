package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericSensorTest {

    private GenericSensor sensor;

    @BeforeEach
    void createSensor() {
        this.sensor = new GenericSensor("sensor");
    }

    @Test
    void testGetDeviceType() {
        /*
         * This is not a typo. Unlike most other features in this module,
         * a sensor is abstract enough that it could be present on a device
         * other than a controller.
         */
        assertSame(IoDevice.class, sensor.getDeviceType());
    }

    @Test
    void testGetState() {
        IoDevice device = mock(IoDevice.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(device);

        SensorValueZ internal = sensor.getInternalState(observer);
        assertNotNull(internal);

        SensorValue container = sensor.getContainerState(internal);
        assertNotNull(container);
    }

}
