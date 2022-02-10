package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import io.ketill.MappedFeatureRegistry;
import io.ketill.RegisteredFeature;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
@ExtendWith(MockitoExtension.class)
class XboxAdapterTest {

    XInputDevice xDevice;
    XInputAxes xAxes;
    XInputButtons xButtons;

    XboxController controller;
    MappedFeatureRegistry registry;
    XboxAdapter adapter;

    @BeforeEach
    void setup() {
        this.xDevice = mock(XInputDevice.class);
        this.xAxes = mock(XInputAxes.class);
        this.xButtons = mock(XInputButtons.class);

        XInputComponents xComponents = mock(XInputComponents.class);
        when(xDevice.getComponents()).thenReturn(xComponents);
        when(xComponents.getButtons()).thenReturn(xButtons);
        when(xComponents.getAxes()).thenReturn(xAxes);
        when(xAxes.get(any())).thenReturn(0.0F);

        this.controller = new XboxController(((d, r) -> {
            this.registry = r;
            this.adapter = new XboxAdapter(d, r, xDevice);
            return adapter;
        }));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void ensureAllFeaturesMapped() {
        for (RegisteredFeature<?, ?> rf : controller.getFeatures()) {
            assertTrue(registry.hasMapping(rf.feature));
        }
    }

    @Test
    void updateButton() {
        /*
         * While there are far more than two buttons, it would be
         * redundant to check all of them here. Two buttons should
         * enough to ensure full functionality via mocking.
         */
        xButtons.a = true;
        xButtons.b = false;

        controller.poll(); /* update button states */

        assertTrue(controller.a.pressed());
        assertFalse(controller.b.pressed());
    }

    @Test
    void updateStick() {
        when(xAxes.get(XInputAxis.LEFT_THUMBSTICK_X)).thenReturn(0.123F);
        when(xAxes.get(XInputAxis.LEFT_THUMBSTICK_Y)).thenReturn(0.456F);
        xButtons.lThumb = false;

        when(xAxes.get(XInputAxis.RIGHT_THUMBSTICK_X)).thenReturn(0.789F);
        when(xAxes.get(XInputAxis.RIGHT_THUMBSTICK_Y)).thenReturn(1.011F);
        xButtons.rThumb = true;

        controller.poll(); /* update stick positions */

        assertEquals(controller.ls.x(), 0.123F);
        assertEquals(controller.ls.y(), 0.456F);
        assertEquals(controller.ls.z(), 0.0F);

        assertEquals(controller.rs.x(), 0.789F);
        assertEquals(controller.rs.y(), 1.011F);
        assertEquals(controller.rs.z(), -1.0F);
    }

    @Test
    void updateTrigger() {
        when(xAxes.get(XInputAxis.LEFT_TRIGGER)).thenReturn(0.123F);
        when(xAxes.get(XInputAxis.RIGHT_TRIGGER)).thenReturn(0.456F);

        controller.poll(); /* update trigger forces */

        assertEquals(controller.lt.force(), 0.123F);
        assertEquals(controller.rt.force(), 0.456F);
    }

    @Test
    void updateMotor() {
        controller.rumbleCoarse.force = 0.125F;
        controller.rumbleFine.force = 0.875F;

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

        controller.rumbleCoarse.force = -1.0F;
        controller.rumbleFine.force = 2.0F;

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
    void isDeviceConnected() {
        when(xDevice.isConnected()).thenReturn(true);
        controller.poll(); /* update connection status */
        assertTrue(controller.isConnected());

        when(xDevice.isConnected()).thenReturn(false);
        controller.poll(); /* update connection status */
        assertFalse(controller.isConnected());
    }

}