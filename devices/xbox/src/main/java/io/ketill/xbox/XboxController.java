package io.ketill.xbox;

import io.ketill.AdapterSupplier;
import io.ketill.Direction;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import io.ketill.controller.MotorVibration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Microsoft XBOX controller.
 */
public class XboxController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_A = new DeviceButton("a"),
            BUTTON_B = new DeviceButton("b"),
            BUTTON_X = new DeviceButton("x"),
            BUTTON_Y = new DeviceButton("y"),
            BUTTON_LB = new DeviceButton("lb"),
            BUTTON_RB = new DeviceButton("rb"),
            BUTTON_GUIDE = new DeviceButton("menu"),
            BUTTON_START = new DeviceButton("pause"),
            BUTTON_L_THUMB = new DeviceButton("l_thumb"),
            BUTTON_R_THUMB = new DeviceButton("r_thumb"),
            BUTTON_UP = new DeviceButton("up", Direction.UP),
            BUTTON_RIGHT = new DeviceButton("right", Direction.RIGHT),
            BUTTON_DOWN = new DeviceButton("down", Direction.DOWN),
            BUTTON_LEFT = new DeviceButton("left", Direction.LEFT);

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls", BUTTON_L_THUMB),
            STICK_RS = new AnalogStick("rs", BUTTON_R_THUMB);

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

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
    public final @NotNull StickPos
            ls = Objects.requireNonNull(super.ls),
            rs = Objects.requireNonNull(super.rs);

    @FeatureState
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt),
            rt = Objects.requireNonNull(super.rt);

    @FeatureState
    public final @NotNull MotorVibration
            rumbleCoarse = this.getState(MOTOR_COARSE),
            rumbleFine = this.getState(MOTOR_FINE);
    /* @formatter:on */

    /**
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
