package io.ketill.xinput;

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

import static io.ketill.xbox.XboxController.*;

/**
 * An {@link XboxController} adapter using XInput.
 * <p>
 * <b>Adapter quirks:</b> XInput does not return an exact value for the
 * controller's battery level. Rather, it will only return if the battery
 * level is {@code FULL}, {@code MEDIUM}, {@code LOW}, or {@code EMPTY}.
 * Due to this, the exact value must be approximated.
 * <p>
 * Furthermore, the {@code INTERNAL_BATTERY} feature is supported only
 * if XInput v1.4 or higher is available on this machine. Support for
 * the {@code BUTTON_GUIDE} feature is also not guaranteed. It depends
 * on the current installation of XInput.
 * <p>
 * <b>Thread safety:</b> This adapter is <i>thread-safe.</i>
 *
 * @see XInputXboxSeeker
 * @see XInput#getPlayer(int)
 */
public final class XInputXboxAdapter extends IoDeviceAdapter<XboxController> {

    private static final int RUMBLE_MIN = 0x0000;
    private static final int RUMBLE_MAX = 0xFFFF;

    private final AtomicXInputDevice xDevice;
    private int rumbleCoarse, rumbleFine;

    XInputXboxAdapter(@NotNull XboxController controller,
                      @NotNull MappedFeatureRegistry registry,
                      @NotNull AtomicXInputDevice xDevice) {
        super(controller, registry);
        this.xDevice = xDevice;
    }

    @MappingMethod
    private void mapXButton(@NotNull ControllerButton button,
                            @NotNull XInputButtonAccessor accessor) {
        registry.mapFeature(button, accessor, this::updateButton);
    }

    @MappingMethod
    private void mapXStick(@NotNull AnalogStick stick,
                           @NotNull XInputAxisAccessor xAxis,
                           @NotNull XInputAxisAccessor yAxis,
                           @Nullable XInputButtonAccessor zAccessor) {
        StickMapping mapping = new StickMapping(xAxis, yAxis, zAccessor);
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    @MappingMethod
    private void mapXTrigger(@NotNull AnalogTrigger trigger,
                             @NotNull XInputAxisAccessor axis) {
        registry.mapFeature(trigger, axis, this::updateTrigger);
    }

    @Override
    protected void initAdapter() {
        this.mapXButton(BUTTON_A, b -> b.a);
        this.mapXButton(BUTTON_B, b -> b.b);
        this.mapXButton(BUTTON_X, b -> b.x);
        this.mapXButton(BUTTON_Y, b -> b.y);
        this.mapXButton(BUTTON_LB, b -> b.lShoulder);
        this.mapXButton(BUTTON_RB, b -> b.rShoulder);
        this.mapXButton(BUTTON_BACK, b -> b.back);
        this.mapXButton(BUTTON_START, b -> b.start);
        this.mapXButton(BUTTON_L_THUMB, b -> b.lThumb);
        this.mapXButton(BUTTON_R_THUMB, b -> b.rThumb);
        this.mapXButton(BUTTON_UP, b -> b.up);
        this.mapXButton(BUTTON_RIGHT, b -> b.right);
        this.mapXButton(BUTTON_DOWN, b -> b.down);
        this.mapXButton(BUTTON_LEFT, b -> b.left);

        if (xDevice.supportsGuideButton()) {
            this.mapXButton(BUTTON_GUIDE, b -> b.guide);
        }

        this.mapXStick(STICK_LS, a -> a.lx, a -> a.ly, b -> b.lThumb);
        this.mapXStick(STICK_RS, a -> a.rx, a -> a.ry, a -> a.rThumb);

        this.mapXTrigger(TRIGGER_LT, axes -> axes.lt);
        this.mapXTrigger(TRIGGER_RT, axes -> axes.rt);

        if (xDevice.supportsBatteryLevel()) {
            registry.mapFeature(INTERNAL_BATTERY, this::updateBattery);
        }

        registry.mapFeature(MOTOR_COARSE, this::updateMotor);
        registry.mapFeature(MOTOR_FINE, this::updateMotor);
    }

    @FeatureAdapter
    private void updateButton(@NotNull ButtonStateZ state,
                              @NotNull XInputButtonAccessor button) {
        state.pressed = xDevice.isPressed(button);
    }

    @FeatureAdapter
    private void updateStick(@NotNull StickPosZ state,
                             @NotNull StickMapping mapping) {
        state.pos.x = xDevice.getAxis(mapping.xAxis);
        state.pos.y = xDevice.getAxis(mapping.yAxis);

        XInputButtonAccessor zButton = mapping.zButton;
        if (zButton != null && xDevice.isPressed(zButton)) {
            state.pos.z = -1.0F;
        } else {
            state.pos.z = 0.0F;
        }
    }

    @FeatureAdapter
    private void updateTrigger(@NotNull TriggerStateZ state,
                               @NotNull XInputAxisAccessor axis) {
        state.force = xDevice.getAxis(axis);
    }

    @FeatureAdapter
    private void updateBattery(@NotNull BatteryLevelZ state) {
        state.level = xDevice.getBatteryLevel();
    }

    @FeatureAdapter
    private void updateMotor(@NotNull MotorVibration state,
                             @NotNull RumbleMotor motor) {
        /*
         * The XInput API will throw an exception if it receives a motor
         * force that is out of its valid bounds. Clamping the force will
         * prevent this from occurring.
         */
        int force = (int) Math.ceil(RUMBLE_MAX * state.getStrength());
        force = Math.min(Math.max(force, RUMBLE_MIN), RUMBLE_MAX);

        /*
         * A comparison is made here to ensure that a vibration force update
         * is only sent when necessary. It would likely lower performance if
         * these signals were dispatched every update call.
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
        xDevice.poll(); /* update components */
    }

    @Override
    protected boolean isDeviceConnected() {
        /*
         * This must return an up-to-date connection status, regardless
         * if the device was polled previously. As a result, the XInput
         * device must be polled here.
         */
        xDevice.poll(); /* update connection status */
        return xDevice.isConnected();
    }

}
