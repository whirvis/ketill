package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputBatteryInformation;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputBatteryLevel;
import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.BatteryLevelZ;
import io.ketill.controller.ButtonStateZ;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.ketill.xbox.XboxController.*;

/**
 * An {@link XboxController} adapter using X-input.
 * <p>
 * <b>Note:</b> X-input does not return an exact value for the controller's
 * battery level. As such, the battery level must be approximated.<br>
 * Furthermore, determining the battery level is only functional if the
 * current version of X-input on this machine is 1.4 or higher.
 */
public final class XInputXboxAdapter extends IoDeviceAdapter<XboxController> {

    private static final int RUMBLE_MIN = 0x0000;
    private static final int RUMBLE_MAX = 0xFFFF;

    private final XInputDevice xDevice;
    private final XInputDevice14 xDevice14;
    private XInputButtons buttons;
    private XInputAxes axes;
    private int rumbleCoarse;
    private int rumbleFine;

    /**
     * Constructs a new {@code XInputXboxAdapter}.
     *
     * @param controller the controller which owns this adapter.
     * @param registry   the controller's mapped feature registry.
     * @param xDevice    the X-input device.
     * @throws NullPointerException if {@code controller}, {@code registry},
     *                              or {@code xDevice} are {@code null}.
     * @throws XInputException      if X-input is not available.
     * @see XInputStatus#isAvailable()
     */
    public XInputXboxAdapter(@NotNull XboxController controller,
                             @NotNull MappedFeatureRegistry registry,
                             @NotNull XInputDevice xDevice) {
        super(controller, registry);
        XInputStatus.requireAvailable();
        this.xDevice = Objects.requireNonNull(xDevice,
                "xDevice cannot be null");

        /*
         * If the X-input device is from X-input 1.4, then cast it here
         * so casting isn't necessary in the future. Instead, a null check
         * will be used to determine if 1.4 is the current version.
         */
        if (xDevice instanceof XInputDevice14) {
            this.xDevice14 = (XInputDevice14) xDevice;
        } else {
            this.xDevice14 = null;
        }
    }

    @MappingMethod
    private void mapXButton(@NotNull ControllerButton button,
                            @NotNull XInputButton accessor) {
        registry.mapFeature(button, accessor, this::updateButton);
    }

    @MappingMethod
    private void mapXStick(@NotNull AnalogStick stick,
                           @NotNull XInputAxis xAxis,
                           @NotNull XInputAxis yAxis,
                           @Nullable XInputButton zReader) {
        StickMapping mapping = new StickMapping(xAxis, yAxis, zReader);
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    @MappingMethod
    private void mapXTrigger(@NotNull AnalogTrigger trigger,
                             @NotNull XInputAxis axis) {
        registry.mapFeature(trigger, axis, this::updateTrigger);
    }

    @MappingMethod
    private void mapXMotor(@NotNull RumbleMotor motor) {
        registry.mapFeature(motor, this::updateMotor);
    }

    @Override
    protected void initAdapter() {
        this.mapXButton(BUTTON_A, xb -> xb.a);
        this.mapXButton(BUTTON_B, xb -> xb.b);
        this.mapXButton(BUTTON_X, xb -> xb.x);
        this.mapXButton(BUTTON_Y, xb -> xb.y);
        this.mapXButton(BUTTON_LB, xb -> xb.lShoulder);
        this.mapXButton(BUTTON_RB, xb -> xb.rShoulder);
        this.mapXButton(BUTTON_GUIDE, xb -> xb.guide);
        this.mapXButton(BUTTON_START, xb -> xb.start);
        this.mapXButton(BUTTON_L_THUMB, xb -> xb.lThumb);
        this.mapXButton(BUTTON_R_THUMB, xb -> xb.rThumb);
        this.mapXButton(BUTTON_UP, xb -> xb.up);
        this.mapXButton(BUTTON_RIGHT, xb -> xb.right);
        this.mapXButton(BUTTON_DOWN, xb -> xb.down);
        this.mapXButton(BUTTON_LEFT, xb -> xb.left);

        this.mapXStick(STICK_LS, XInputAxis.LEFT_THUMBSTICK_X,
                XInputAxis.LEFT_THUMBSTICK_Y, xb -> xb.lThumb);
        this.mapXStick(STICK_RS, XInputAxis.RIGHT_THUMBSTICK_X,
                XInputAxis.RIGHT_THUMBSTICK_Y, xb -> xb.rThumb);

        this.mapXTrigger(TRIGGER_LT, XInputAxis.LEFT_TRIGGER);
        this.mapXTrigger(TRIGGER_RT, XInputAxis.RIGHT_TRIGGER);

        registry.mapFeature(INTERNAL_BATTERY, this::updateBattery);

        this.mapXMotor(MOTOR_COARSE);
        this.mapXMotor(MOTOR_FINE);
    }

    @FeatureAdapter
    private void updateButton(@NotNull ButtonStateZ state,
                              @NotNull XInputButton button) {
        state.pressed = button.isPressed(buttons);
    }

    @FeatureAdapter
    private void updateStick(@NotNull StickPosZ state,
                             @NotNull StickMapping mapping) {
        state.pos.x = axes.get(mapping.xAxis);
        state.pos.y = axes.get(mapping.yAxis);

        XInputButton zButton = mapping.zButton;
        if (zButton != null && zButton.isPressed(buttons)) {
            state.pos.z = -1.0F;
        } else {
            state.pos.z = 0.0F;
        }
    }

    @FeatureAdapter
    private void updateTrigger(@NotNull TriggerStateZ state,
                               @NotNull XInputAxis axis) {
        state.force = axes.get(axis);
    }

    @FeatureAdapter
    private void updateBattery(@NotNull BatteryLevelZ state) {
        if (xDevice14 == null) {
            return; /* battery level unknown */
        }

        XInputBatteryDeviceType deviceType = XInputBatteryDeviceType.GAMEPAD;
        XInputBatteryInformation batteryInfo =
                xDevice14.getBatteryInformation(deviceType);
        XInputBatteryLevel batteryLevel = batteryInfo.getLevel();

        if (batteryLevel == null) {
            state.level = -1.0F;
        } else {
            switch (batteryLevel) {
                case EMPTY:
                    state.level = 0.00F;
                    break;
                case LOW:
                    state.level = 0.25F;
                    break;
                case MEDIUM:
                    state.level = 0.50F;
                    break;
                case FULL:
                    state.level = 1.00F;
                    break;
                default:
                    throw new XInputException("this is a bug");
            }
        }
    }

    @FeatureAdapter
    private void updateMotor(@NotNull MotorVibration state,
                             @NotNull RumbleMotor motor) {
        /*
         * The X-input API will throw an exception if it receives a motor
         * force that is out of its valid bounds. Clamping the force will
         * prevent this from occurring.
         */
        int force = (int) Math.ceil(RUMBLE_MAX * state.getStrength());
        force = Math.min(Math.max(force, RUMBLE_MIN), RUMBLE_MAX);

        /*
         * A comparison is made here to ensure that a vibration force update
         * is only sent when necessary. It would lower performance if these
         * signals were sent every update call.
         */
        if (motor == MOTOR_COARSE && rumbleCoarse != force) {
            this.rumbleCoarse = force;
            synchronized (xDevice) {
                xDevice.setVibration(rumbleCoarse, rumbleFine);
            }
        } else if (motor == MOTOR_FINE && rumbleFine != force) {
            this.rumbleFine = force;
            synchronized (xDevice) {
                xDevice.setVibration(rumbleCoarse, rumbleFine);
            }
        }
    }

    @Override
    protected void pollDevice() {
        synchronized (xDevice) {
            xDevice.poll();
        }
        XInputComponents comps = xDevice.getComponents();
        this.axes = comps.getAxes();
        this.buttons = comps.getButtons();
    }

    @Override
    protected boolean isDeviceConnected() {
        /*
         * isDeviceConnected() promises to return an up-to-date connection
         * status, regardless of whether pollDevice() was previously called.
         * As such, the X-input device must be polled here.
         */
        synchronized (xDevice) {
            xDevice.poll();
        }
        return xDevice.isConnected();
    }

}
