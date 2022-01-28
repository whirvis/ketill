package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.whirvis.kibasan.AnalogStick;
import com.whirvis.kibasan.AnalogTrigger;
import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.DeviceButton;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.InputException;
import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.RumbleMotor;
import com.whirvis.kibasan.Trigger1f;
import com.whirvis.kibasan.Vibration1f;
import com.whirvis.kibasan.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Field;

import static com.whirvis.kibasan.xbox.XboxController.*;

public final class XInputAdapter extends DeviceAdapter<XboxController> {

    private static final int RUMBLE_MIN = 0x0000;
    private static final int RUMBLE_MAX = 0xFFFF;

    private final XInputDevice xDevice;
    private XInputButtons buttons;
    private XInputAxes axes;
    private int rumbleCoarse;
    private int rumbleFine;

    public XInputAdapter(@NotNull XboxController controller,
                         @NotNull MappedFeatureRegistry registry,
                         @NotNull XInputDevice xDevice) {
        super(controller, registry);
        this.xDevice = xDevice;
    }

    private boolean isPressed(@Nullable Field field) {
        if (field == null) {
            return false;
        }
        try {
            return field.getBoolean(buttons);
        } catch (IllegalAccessException e) {
            throw new InputException(e);
        }
    }

    @Nullable
    private Field getButtonField(@Nullable String fieldName) {
        if (fieldName == null) {
            return null;
        }
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

    private void mapXButton(@NotNull DeviceButton button,
                            @NotNull String buttonFieldName) {
        Field field = this.getButtonField(buttonFieldName);
        registry.mapFeature(button, field, this::updateButton);
    }

    private void mapXStick(@NotNull AnalogStick stick,
                           @NotNull XInputAxis xAxis,
                           @NotNull XInputAxis yAxis,
                           @Nullable String zButtonFieldName) {
        Field zButtonField = this.getButtonField(zButtonFieldName);
        XStickMapping m = new XStickMapping(xAxis, yAxis, zButtonField);
        registry.mapFeature(stick, m, this::updateStick);
    }

    private void mapXTrigger(@NotNull AnalogTrigger trigger,
                             @NotNull XInputAxis axis) {
        registry.mapFeature(trigger, axis, this::updateTrigger);
    }

    private void mapXMotor(@NotNull RumbleMotor motor) {
        registry.mapFeature(motor, this::updateMotor);
    }

    @Override
    public void initAdapter() {
        this.mapXButton(BUTTON_A, "a");
        this.mapXButton(BUTTON_B, "b");
        this.mapXButton(BUTTON_X, "x");
        this.mapXButton(BUTTON_Y, "y");
        this.mapXButton(BUTTON_LB, "lShoulder");
        this.mapXButton(BUTTON_RB, "rShoulder");
        this.mapXButton(BUTTON_GUIDE, "guide");
        this.mapXButton(BUTTON_START, "start");
        this.mapXButton(BUTTON_L_THUMB, "lThumb");
        this.mapXButton(BUTTON_R_THUMB, "rThumb");
        this.mapXButton(BUTTON_UP, "up");
        this.mapXButton(BUTTON_RIGHT, "right");
        this.mapXButton(BUTTON_DOWN, "down");
        this.mapXButton(BUTTON_LEFT, "left");

        this.mapXStick(STICK_LS, XInputAxis.LEFT_THUMBSTICK_X,
                XInputAxis.LEFT_THUMBSTICK_Y, "lThumb");
        this.mapXStick(STICK_RS, XInputAxis.RIGHT_THUMBSTICK_X,
                XInputAxis.RIGHT_THUMBSTICK_Y, "rThumb");

        this.mapXTrigger(TRIGGER_LT, XInputAxis.LEFT_TRIGGER);
        this.mapXTrigger(TRIGGER_RT, XInputAxis.RIGHT_TRIGGER);

        this.mapXMotor(MOTOR_COARSE);
        this.mapXMotor(MOTOR_FINE);
    }

    @FeatureAdapter
    private void updateButton(@NotNull Button1b button, @NotNull Field field) {
        button.pressed = this.isPressed(field);
    }

    @FeatureAdapter
    private void updateStick(@NotNull Vector3f stick,
                             @NotNull XStickMapping mapping) {
        stick.x = axes.get(mapping.xAxis);
        stick.y = axes.get(mapping.yAxis);
        stick.z = this.isPressed(mapping.zButtonField) ? -1.0F : 0.0F;
    }

    @FeatureAdapter
    private void updateTrigger(@NotNull Trigger1f trigger,
                               @NotNull XInputAxis axis) {
        trigger.force = axes.get(axis);
    }

    @FeatureAdapter
    private void updateMotor(Vibration1f vibration, RumbleMotor motor) {
        /*
         * The X-input API will throw an exception if it receives a motor force
         * that is out of its valid bounds. Clamping the force will prevent this
         * from occurring.
         */
        int force = (int) (RUMBLE_MAX * vibration.force);
        force = Math.min(Math.max(force, RUMBLE_MIN), RUMBLE_MAX);

        /*
         * A comparison is made here to ensure that a vibration force update is
         * only sent when necessary. It would be horrendous for performance to
         * send these signals every update call.
         */
        if (motor == MOTOR_COARSE && rumbleCoarse != force) {
            this.rumbleCoarse = force;
            xDevice.setVibration(rumbleCoarse, rumbleFine);
        } else if (motor == MOTOR_FINE && rumbleFine != force) {
            this.rumbleFine = force;
            xDevice.setVibration(rumbleCoarse, rumbleFine);
        }
    }

    @Override
    protected void pollDevice() {
        xDevice.poll();
        XInputComponents comps = xDevice.getComponents();
        this.axes = comps.getAxes();
        this.buttons = comps.getButtons();
    }

    @Override
    protected boolean isDeviceConnected() {
        return xDevice.isConnected();
    }

}
