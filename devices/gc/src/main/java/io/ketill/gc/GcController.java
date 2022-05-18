package io.ketill.gc;

import io.ketill.AdapterSupplier;
import io.ketill.controller.Direction;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import io.ketill.controller.MotorVibration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Nintendo GameCube controller.
 */
public class GcController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_A = new ControllerButton("a"),
            BUTTON_B = new ControllerButton("b"),
            BUTTON_X = new ControllerButton("x"),
            BUTTON_Y = new ControllerButton("y"),
            BUTTON_LEFT = new ControllerButton("left", Direction.LEFT),
            BUTTON_RIGHT = new ControllerButton("right", Direction.RIGHT),
            BUTTON_DOWN = new ControllerButton("down", Direction.DOWN),
            BUTTON_UP = new ControllerButton("up", Direction.UP),
            BUTTON_START = new ControllerButton("start"),
            BUTTON_Z = new ControllerButton("z"),
            BUTTON_R = new ControllerButton("r"),
            BUTTON_L = new ControllerButton("l");

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls"),
            STICK_RS = new AnalogStick("rs");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_RUMBLE = new RumbleMotor("rumble");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            a = this.getState(BUTTON_A),
            b = this.getState(BUTTON_B),
            x = this.getState(BUTTON_X),
            y = this.getState(BUTTON_Y),
            left = this.getState(BUTTON_LEFT),
            right = this.getState(BUTTON_RIGHT),
            down = this.getState(BUTTON_DOWN),
            up = this.getState(BUTTON_UP),
            start = this.getState(BUTTON_START),
            z = this.getState(BUTTON_Z),
            r = this.getState(BUTTON_R),
            l = this.getState(BUTTON_L);

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
            rumble = this.getState(MOTOR_RUMBLE);
    /* @formatter:on */

    /**
     * @param adapterSupplier the GameCube controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public GcController(@NotNull AdapterSupplier<GcController> adapterSupplier) {
        super("gc", adapterSupplier,
                STICK_LS, STICK_RS, TRIGGER_LT, TRIGGER_RT);
    }

}
