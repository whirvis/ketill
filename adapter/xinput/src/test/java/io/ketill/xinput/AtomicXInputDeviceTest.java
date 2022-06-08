package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputBatteryInformation;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputBatteryLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Even though these tests for X-input use heavy mocking, they will
 * only work on Windows due to how the native libraries are loaded.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@EnabledOnOs(OS.WINDOWS)
class AtomicXInputDeviceTest {

    private XInputDevice14 xDevice;
    private XInputAxes xAxes;
    private XInputButtons xButtons;
    private XInputBatteryInformation xBatteryInfo;

    private AtomicXInputDevice atomic;

    @BeforeEach
    void createAtomic() {
        this.xDevice = mock(XInputDevice14.class);
        this.xAxes = mock(XInputAxes.class);
        this.xButtons = mock(XInputButtons.class);
        this.xBatteryInfo = mock(XInputBatteryInformation.class);

        XInputComponents xComps = mock(XInputComponents.class);
        when(xComps.getButtons()).thenReturn(xButtons);
        when(xComps.getAxes()).thenReturn(xAxes);

        /*
         * The mocks below ensures the atomic wrapper report the desired
         * data. Furthermore, device.isConnected() must return a value of
         * true. Otherwise, the wrapper will not return the components or
         * battery information that's been provided for testing.
         */
        /* @formatter:off */
        when(xDevice.isConnected()).thenReturn(true);
        when(xDevice.getComponents()).thenReturn(xComps);
        when(xDevice.getBatteryInformation(XInputBatteryDeviceType.GAMEPAD))
                .thenReturn(xBatteryInfo);
        /* @formatter:on */

        this.atomic = new AtomicXInputDevice(xDevice);
    }

    @Test
    void testSupportsGuideButton() {
        /*
         * The supportsGuideButton() should call on XInputDevice to see
         * if the guide button is actually supported on this machine.
         */
        try (MockedStatic<XInputDevice> x10 = mockStatic(XInputDevice.class)) {
            x10.when(XInputDevice::isGuideButtonSupported).thenReturn(true);
            assertTrue(atomic.supportsGuideButton());
            x10.when(XInputDevice::isGuideButtonSupported).thenReturn(false);
            assertFalse(atomic.supportsGuideButton());
        }
    }

    @Test
    void testSupportsBatteryLevel() {
        /*
         * Since the mock device is an XInputDevice14, the wrapper will
         * have access to the battery level. As such, this method should
         * return a value of true.
         */
        assertTrue(atomic.supportsBatteryLevel());
    }

    @Test
    void testIsConnected() {
        when(xDevice.isConnected()).thenReturn(true);
        atomic.poll(); /* update connection status */
        assertTrue(atomic.isConnected());

        when(xDevice.isConnected()).thenReturn(false);
        atomic.poll(); /* update connection status */
        assertFalse(atomic.isConnected());
    }

    @Test
    void testIsPressed() {
        xButtons.a = true;
        atomic.poll(); /* update buttons */
        assertTrue(atomic.isPressed(b -> b.a));

        /*
         * When the device is not connected, the internal buttons state
         * should be nullified as their state is now unknown. As such,
         * this method should now return false even though the A button
         * has been mocked to be pressed.
         */
        when(xDevice.isConnected()).thenReturn(false);
        atomic.poll(); /* update buttons */
        assertFalse(atomic.isPressed(b -> b.a));
    }

    @Test
    void testGetAxis() {
        xAxes.lx = 1.23F;
        atomic.poll(); /* update axes */
        assertEquals(1.23F, atomic.getAxis(a -> a.lx));

        /*
         * When the device is not connected, the internal axes state
         * should be nullified as their state is now unknown. As such,
         * this method should now return zero even though the LX axis
         * has been mocked to have a value of 1.23F.
         */
        when(xDevice.isConnected()).thenReturn(false);
        atomic.poll(); /* update axes */
        assertEquals(0.00F, atomic.getAxis(a -> a.lx));
    }

    @Test
    void testGetBatteryLevel() {
        /*
         * When the battery level is null, assume that X-input does not
         * know what the battery level is. This means we would not know
         * what the battery level is either. As such, this should result
         * in the battery level being set to -1.0F.
         */
        when(xBatteryInfo.getLevel()).thenReturn(null);
        atomic.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_UNKNOWN, atomic.getBatteryLevel());

        /* verify EMPTY results in corresponding battery level */
        when(xBatteryInfo.getLevel()).thenReturn(XInputBatteryLevel.EMPTY);
        atomic.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_EMPTY, atomic.getBatteryLevel());

        /* verify LOW results in corresponding battery level */
        when(xBatteryInfo.getLevel()).thenReturn(XInputBatteryLevel.LOW);
        atomic.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_LOW, atomic.getBatteryLevel());

        /* verify MEDIUM results in corresponding battery level */
        when(xBatteryInfo.getLevel()).thenReturn(XInputBatteryLevel.MEDIUM);
        atomic.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_MEDIUM, atomic.getBatteryLevel());

        /* verify FULL results in corresponding battery level */
        when(xBatteryInfo.getLevel()).thenReturn(XInputBatteryLevel.FULL);
        atomic.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_FULL, atomic.getBatteryLevel());
    }

    @Test
    void testSetVibration() {
        atomic.setVibration(0x1234, 0x5678);
        verify(xDevice).setVibration(0x1234, 0x5678);
    }

    @Test
    void testPoll() {
        /* disconnect device for next test */
        when(xDevice.isConnected()).thenReturn(false);
        atomic.poll(); /* update components */

        /*
         * When the device is not connected, neither getComponents()
         * nor getBatteryInformation() should be called by the atomic
         * wrapper. Instead, the internal values for the components,
         * battery level, etc. should all be nullified as their state
         * is no longer known.
         */
        verify(xDevice, times(1)).poll();
        verify(xDevice, never()).getComponents();
        verify(xDevice, never()).getBatteryInformation(any());

        /* connect device for next test */
        when(xDevice.isConnected()).thenReturn(true);
        atomic.poll(); /* update components */

        /*
         * Since the device is now connected, both the getComponents()
         * and getBatteryInformation() methods should be called so the
         * atomic wrapper can return the current state of the device.
         */
        verify(xDevice, times(2)).poll();
        verify(xDevice, times(1)).getComponents();
        verify(xDevice, times(1)).getBatteryInformation(any());
    }

}
