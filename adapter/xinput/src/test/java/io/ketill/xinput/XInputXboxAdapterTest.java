package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@EnabledOnOs(OS.WINDOWS)
class XInputXboxAdapterTest {

    private XInputDevice xDevice;
    private XInputAxes xAxes;
    private XInputButtons xButtons;

    private XboxController controller;
    private XInputXboxAdapter adapter;

    @BeforeEach
    void createAdapter() {
        assertTrue(XInputStatus.isAvailable());
        assertDoesNotThrow(XInputStatus::requireAvailable);

        this.xDevice = mock(XInputDevice.class);
        this.xAxes = mock(XInputAxes.class);
        this.xButtons = mock(XInputButtons.class);

        XInputComponents xComponents = mock(XInputComponents.class);
        when(xDevice.getComponents()).thenReturn(xComponents);
        when(xComponents.getButtons()).thenReturn(xButtons);
        when(xComponents.getAxes()).thenReturn(xAxes);
        when(xAxes.get(any())).thenReturn(0.0F);

        this.controller = new XboxController(((d, r) -> {
            this.adapter = new XInputXboxAdapter(d, r, xDevice);
            return adapter;
        }));
    }

    @Test
    void ensureAllFeaturesSupported() {
        assertAllFeaturesSupported(controller);
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
        when(xAxes.get(XInputAxis.LEFT_THUMBSTICK_X)).thenReturn(0.123F);
        when(xAxes.get(XInputAxis.LEFT_THUMBSTICK_Y)).thenReturn(0.456F);
        xButtons.lThumb = false;

        when(xAxes.get(XInputAxis.RIGHT_THUMBSTICK_X)).thenReturn(0.789F);
        when(xAxes.get(XInputAxis.RIGHT_THUMBSTICK_Y)).thenReturn(1.011F);
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
        when(xAxes.get(XInputAxis.LEFT_TRIGGER)).thenReturn(0.123F);
        when(xAxes.get(XInputAxis.RIGHT_TRIGGER)).thenReturn(0.456F);

        controller.poll(); /* update trigger forces */

        assertEquals(0.123F, controller.lt.getForce());
        assertEquals(0.456F, controller.rt.getForce());
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
        controller.poll(); /* update connection status */
        assertTrue(controller.isConnected());

        when(xDevice.isConnected()).thenReturn(false);
        controller.poll(); /* update connection status */
        assertFalse(controller.isConnected());
    }

}