package io.ketill.xbox;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.BatteryLevel;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.Direction;
import io.ketill.controller.InternalBattery;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Microsoft XBOX controller.
 */
public class XboxController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_A = new ControllerButton("a"),
            BUTTON_B = new ControllerButton("b"),
            BUTTON_X = new ControllerButton("x"),
            BUTTON_Y = new ControllerButton("y"),
            BUTTON_LB = new ControllerButton("lb"),
            BUTTON_RB = new ControllerButton("rb"),
            BUTTON_GUIDE = new ControllerButton("guide"),
            BUTTON_BACK = new ControllerButton("back"),
            BUTTON_START = new ControllerButton("start"),
            BUTTON_L_THUMB = new ControllerButton("l_thumb"),
            BUTTON_R_THUMB = new ControllerButton("r_thumb"),
            BUTTON_UP = new ControllerButton("up", Direction.UP),
            BUTTON_RIGHT = new ControllerButton("right", Direction.RIGHT),
            BUTTON_DOWN = new ControllerButton("down", Direction.DOWN),
            BUTTON_LEFT = new ControllerButton("left", Direction.LEFT);

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls", BUTTON_L_THUMB),
            STICK_RS = new AnalogStick("rs", BUTTON_R_THUMB);

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull InternalBattery
            INTERNAL_BATTERY = new InternalBattery("battery");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_COARSE = new RumbleMotor("rumble_coarse"),
            MOTOR_FINE = new RumbleMotor("rumble_fine");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            a = this.getState(BUTTON_A),
            b = this.getState(BUTTON_B),
            x = this.getState(BUTTON_X),
            y = this.getState(BUTTON_Y),
            lb = this.getState(BUTTON_LB),
            rb = this.getState(BUTTON_RB),
            guide = this.getState(BUTTON_GUIDE),
            start = this.getState(BUTTON_START),
            lThumb = this.getState(BUTTON_L_THUMB),
            rThumb = this.getState(BUTTON_R_THUMB),
            up = this.getState(BUTTON_UP),
            right = this.getState(BUTTON_RIGHT),
            down = this.getState(BUTTON_DOWN),
            left = this.getState(BUTTON_LEFT);

    @FeatureState
    @SuppressWarnings("HidingField")
    public final @NotNull StickPos
            ls = Objects.requireNonNull(super.ls),
            rs = Objects.requireNonNull(super.rs);

    @FeatureState
    @SuppressWarnings("HidingField")
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt),
            rt = Objects.requireNonNull(super.rt);

    @FeatureState
    public final @NotNull BatteryLevel
            battery = this.getState(INTERNAL_BATTERY);

    @FeatureState
    public final @NotNull MotorVibration
            rumbleCoarse = this.getState(MOTOR_COARSE),
            rumbleFine = this.getState(MOTOR_FINE);
    /* @formatter:on */

    /**
     * Constructs a new {@code XboxController}.
     *
     * @param adapterSupplier the XBOX controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public XboxController(@NotNull AdapterSupplier<XboxController> adapterSupplier) {
        super("xbox", adapterSupplier,
                STICK_LS, STICK_RS, TRIGGER_LT, TRIGGER_RT);
    }

}
