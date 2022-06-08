package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputBatteryInformation;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputBatteryLevel;
import io.ketill.IoFeature;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.ArrayList;
import java.util.List;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Even though these tests for X-input use heavy mocking, they will
 * only work on Windows due to how the native libraries are loaded.
 */
@EnabledOnOs(OS.WINDOWS)
class XInputXboxAdapterTest {

    private XInputDevice14 xDevice;
    private XInputAxes xAxes;
    private XInputButtons xButtons;
    private XInputBatteryInformation xBatteryInfo;

    private AtomicXInputDevice axDevice;
    private XboxController controller;

    @BeforeEach
    void createAdapter() {
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

        this.controller = new XboxController(((c, r) -> {
            this.axDevice = new AtomicXInputDevice(xDevice);
            return new XInputXboxAdapter(c, r, axDevice);
        }));
    }

    @Test
    void ensureAllFeaturesSupported() {
        List<IoFeature<?, ?>> unsupported = new ArrayList<>();
        if (!axDevice.supportsGuideButton()) {
            unsupported.add(XboxController.BUTTON_GUIDE);
        }
        if (!axDevice.supportsBatteryLevel()) {
            unsupported.add(XboxController.INTERNAL_BATTERY);
        }
        assertAllFeaturesSupported(controller, unsupported);
    }

    @Test
    void testUpdateButton() {
        /*
         * While there are far more than two buttons, it would be redundant
         * to check all of them here. Two buttons should enough to ensure
         * full functionality via mocking.
         */
        xButtons.a = true;
        xButtons.b = false;

        controller.poll(); /* update button states */

        assertTrue(controller.a.isPressed());
        assertFalse(controller.b.isPressed());
    }

    @Test
    void testUpdateStick() {
        xAxes.lx = 0.123F;
        xAxes.ly = 0.456F;
        xButtons.lThumb = false;

        xAxes.rx = 0.789F;
        xAxes.ry = 1.011F;
        xButtons.rThumb = true;

        controller.poll(); /* update stick positions */

        assertEquals(0.123F, controller.ls.getX());
        assertEquals(0.456F, controller.ls.getY());
        assertEquals(0.0F, controller.ls.getZ());

        assertEquals(0.789F, controller.rs.getX());
        assertEquals(1.011F, controller.rs.getY());
        assertEquals(-1.0F, controller.rs.getZ());
    }

    @Test
    void testUpdateTrigger() {
        xAxes.lt = 0.123F;
        xAxes.rt = 0.456F;

        controller.poll(); /* update trigger forces */

        assertEquals(0.123F, controller.lt.getForce());
        assertEquals(0.456F, controller.rt.getForce());
    }

    @Test
    void testUpdateBattery() {
        when(xBatteryInfo.getLevel()).thenReturn(XInputBatteryLevel.LOW);
        controller.poll(); /* update battery level */
        assertEquals(XInput.BATTERY_LEVEL_LOW, controller.battery.getLevel());
    }

    @Test
    void testUpdateMotor() {
        controller.rumbleCoarse.setStrength(0.125F);
        controller.rumbleFine.setStrength(0.875F);

        /*
         * Once the force of each rumble motor has been set, a controller
         * poll should result in the vibration strengths being updated to
         * their respective values in X-input.
         */
        clearInvocations(xDevice);
        controller.poll(); /* update rumble motors */
        verify(xDevice).setVibration(8192, 57344);

        /*
         * Since the rumble motor forces remain unchanged, no call to
         * setVibration() should be made after this second controller
         * poll. This is done for performance reasons.
         */
        clearInvocations(xDevice);
        controller.poll(); /* update rumble motors */
        verify(xDevice, never()).setVibration(anyInt(), anyInt());

        controller.rumbleCoarse.setStrength(-1.0F);
        controller.rumbleFine.setStrength(2.0F);

        /*
         * If the rumble motor force is out of bounds, it is up to the
         * adapter to clamp the vibration strength back into bounds for
         * X-input. If this is not done, X-input will crash.
         */
        clearInvocations(xDevice);
        controller.poll(); /* update rumble motors */
        verify(xDevice).setVibration(0, 65535);
    }

    @Test
    void testIsDeviceConnected() {
        when(xDevice.isConnected()).thenReturn(true);
        assertTrue(controller.isConnected());
        when(xDevice.isConnected()).thenReturn(false);
        assertFalse(controller.isConnected());
    }

}