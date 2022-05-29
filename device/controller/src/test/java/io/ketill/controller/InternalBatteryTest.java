package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalBatteryTest {

    private InternalBattery battery;

    @BeforeEach
    void createBattery() {
        this.battery = new InternalBattery("battery");
    }

    @Test
    void testGetDeviceType() {
        /*
         * This is not a typo. Unlike most other features in this module,
         * an internal battery is abstract enough that it could be present
         * on a device other than a controller.
         */
        assertSame(IoDevice.class, battery.getDeviceType());
    }

    @Test
    void testGetState() {
        IoDevice device = mock(IoDevice.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(device);

        BatteryLevelZ internal = battery.getInternalState(observer);
        assertNotNull(internal);

        BatteryLevel container = battery.getContainerState(internal);
        assertNotNull(container);
    }

}
