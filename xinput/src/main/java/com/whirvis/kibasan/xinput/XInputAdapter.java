package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.kibasan.AnalogStick;
import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.DeviceButton;
import com.whirvis.kibasan.InputException;
import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.Trigger1f;
import com.whirvis.kibasan.Vibration1f;
import com.whirvis.kibasan.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Field;

import static com.whirvis.kibasan.xbox.XboxController.*;

/**
 * An adapter which maps input for an XBOX controller via X-input.
 */
public class XboxAdapter extends DeviceAdapter<XboxController> {

    private static final int RUMBLE_MIN = 0;
    private static final int RUMBLE_MAX = 65535;

    private final XInputDevice xDevice;
    private XInputButtons buttons;
    private XInputAxes axes;
    private int rumbleCoarse;
    private int rumbleFine;

    public XboxAdapter(@NotNull XInputDevice xDevice) {
        this.xDevice = xDevice;
    }

    @NotNull
    private Field getButtonField(@NotNull String fieldName) {
        try {
            return XInputButtons.class.getField(fieldName);
        } catch (NoSuchFieldException e) {
            String msg = "no such button " + fieldName;
            throw new InputException(msg, e);
        } catch (SecurityException e) {
            String msg = "field " + fieldName + " not accessible";
            throw new InputException(msg, e);
        }
    }

    private void mapXButton(@NotNull MappedFeatureRegistry registry,
                            @NotNull DeviceButton button,
                            @NotNull String buttonFieldName) {
        Field field = this.getButtonField(buttonFieldName);
        registry.mapFeature(button, field, this::isPressed);
    }

    private void mapXStick(@NotNull MappedFeatureRegistry registry,
                           @NotNull AnalogStick stick,
                           @NotNull XInputAxis leftAxis,
                           @NotNull XInputAxis rightAxis,
                           @Nullable String zButtonFieldName) {
        Field zButtonField = this.getButtonField(zButtonFieldName);
        XStickMapping m = new XStickMapping(leftAxis, rightAxis, zButtonField);
        registry.mapFeature(stick, m, this::updateStick);
    }

    @Override
    public void initAdapter(@NotNull XboxController controller,
                            @NotNull MappedFeatureRegistry registry) {
        this.mapXButton(registry, BUTTON_A, "a");
        this.mapXButton(registry, BUTTON_B, "b");
        this.mapXButton(registry, BUTTON_X, "x");
        this.mapXButton(registry, BUTTON_Y, "y");
        this.mapXButton(registry, BUTTON_LB, "lShoulder");
        this.mapXButton(registry, BUTTON_RB, "rShoulder");
        this.mapXButton(registry, BUTTON_GUIDE, "guide");
        this.mapXButton(registry, BUTTON_START, "start");
        this.mapXButton(registry, BUTTON_L_THUMB, "lThumb");
        this.mapXButton(registry, BUTTON_R_THUMB, "rThumb");
        this.mapXButton(registry, BUTTON_UP, "up");
        this.mapXButton(registry, BUTTON_RIGHT, "right");
        this.mapXButton(registry, BUTTON_DOWN, "down");
        this.mapXButton(registry, BUTTON_LEFT, "left");

        this.mapXStick(registry, STICK_LS, XInputAxis.LEFT_THUMBSTICK_X,
                XInputAxis.LEFT_THUMBSTICK_Y, "lThumb");
        this.mapXStick(registry, STICK_RS, XInputAxis.RIGHT_THUMBSTICK_X,
                XInputAxis.RIGHT_THUMBSTICK_Y, "rThumb");

        registry.mapFeature(TRIGGER_LT, XInputAxis.LEFT_TRIGGER,
                this::updateTrigger);
        registry.mapFeature(TRIGGER_RT, XInputAxis.RIGHT_TRIGGER,
                this::updateTrigger);

        registry.mapFeature(MOTOR_COARSE, this::doCoarseRumble);
        registry.mapFeature(MOTOR_FINE, this::doFineRumble);
    }

    private boolean isPressed(@Nullable Field field) {
        try {
            if (field == null) {
                return false;
            }
            return field.getBoolean(buttons);
        } catch (IllegalAccessException e) {
            throw new InputException(e);
        }
    }

    protected void isPressed(@NotNull Button1b button, @NotNull Field field) {
        button.pressed = this.isPressed(field);
    }

    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull XStickMapping mapping) {
        stick.x = axes.get(mapping.xAxis);
        stick.y = axes.get(mapping.yAxis);
        stick.z = this.isPressed(mapping.zButtonField) ? -1.0F : 0.0F;
    }

    protected void updateTrigger(@NotNull Trigger1f trigger,
                                 @NotNull XInputAxis axis) {
        trigger.force = axes.get(axis);
    }

    public void doCoarseRumble(Vibration1f vibration) {
        int force = (int) (RUMBLE_MAX * vibration.force);
        force = Math.max(Math.min(force, RUMBLE_MIN), RUMBLE_MAX);
        if (rumbleCoarse != force) {
            this.rumbleCoarse = force;
            xDevice.setVibration(rumbleCoarse, rumbleFine);
        }
    }


    public void doFineRumble(Vibration1f vibration) {
        int force = (int) (RUMBLE_MAX * vibration.force);
        force = Math.max(Math.min(force, RUMBLE_MIN), RUMBLE_MAX);
        if (rumbleFine != force) {
            this.rumbleFine = force;
            xDevice.setVibration(rumbleCoarse, rumbleFine);
        }
    }

    @Override
    protected void pollDevice(@NotNull XboxController controller) {
        xDevice.poll();
        XInputComponents comps = xDevice.getComponents();
        this.axes = comps.getAxes();
        this.buttons = comps.getButtons();
    }

    @Override
    protected boolean isDeviceConnected(@NotNull XboxController controller) {
        return xDevice.isConnected();
    }

}
