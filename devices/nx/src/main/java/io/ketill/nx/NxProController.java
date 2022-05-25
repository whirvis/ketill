package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogStickCalibration;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.Direction;
import io.ketill.controller.LedState;
import io.ketill.controller.PlayerLed;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Nintendo Switch Pro controller.
 */
public class NxProController extends Controller {

    /* @formatter:off */
    public static final @NotNull AnalogStickCalibration
            CALIBRATION = new AnalogStickCalibration(0.70F, 0.70F, -0.70F, -0.70F);
    /* @formatter:on */

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_B = new ControllerButton("b"),
            BUTTON_A = new ControllerButton("a"),
            BUTTON_Y = new ControllerButton("y"),
            BUTTON_X = new ControllerButton("x"),
            BUTTON_L = new ControllerButton("l"),
            BUTTON_R = new ControllerButton("r"),
            BUTTON_MINUS = new ControllerButton("minus"),
            BUTTON_PLUS = new ControllerButton("plus"),
            BUTTON_L_THUMB = new ControllerButton("l_thumb"),
            BUTTON_R_THUMB = new ControllerButton("r_thumb"),
            BUTTON_HOME = new ControllerButton("home"),
            BUTTON_SCREENSHOT = new ControllerButton("screenshot"),
            BUTTON_BUMPER = new ControllerButton("bumper"),
            BUTTON_Z_BUMPER = new ControllerButton("z_bumper"),
            BUTTON_UP = new ControllerButton("up", Direction.UP),
            BUTTON_RIGHT = new ControllerButton("right", Direction.RIGHT),
            BUTTON_DOWN = new ControllerButton("down", Direction.DOWN),
            BUTTON_LEFT = new ControllerButton("left", Direction.LEFT);

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_LS = new AnalogStick("ls", BUTTON_L_THUMB, CALIBRATION),
            STICK_RS = new AnalogStick("rs", BUTTON_R_THUMB, CALIBRATION);

    /**
     * These features are both presented as an {@link AnalogTrigger} (rather
     * than a {@link ControllerButton}) even though they can only be fully
     * pressed or fully released. This is because most other controllers have
     * a proper analog trigger, so this continues the pattern.
     * <p>
     * Feature adapters should set the force to {@code 1.0F} when the
     * trigger button is pressed, and {@code 0.0F} when it is released.
     */
    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_ZL = new AnalogTrigger("lt"),
            TRIGGER_ZR = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull PlayerLed
            FEATURE_LED = new PlayerLed("led");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            b = this.getState(BUTTON_B),
            a = this.getState(BUTTON_A),
            y = this.getState(BUTTON_Y),
            x = this.getState(BUTTON_X),
            l = this.getState(BUTTON_L),
            r = this.getState(BUTTON_R),
            minus = this.getState(BUTTON_MINUS),
            plus = this.getState(BUTTON_PLUS),
            lThumb = this.getState(BUTTON_L_THUMB),
            rThumb = this.getState(BUTTON_R_THUMB),
            home = this.getState(BUTTON_HOME),
            screenshot = this.getState(BUTTON_SCREENSHOT),
            bumper = this.getState(BUTTON_BUMPER),
            zBumper = this.getState(BUTTON_Z_BUMPER),
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
            zl = Objects.requireNonNull(super.lt),
            zr = Objects.requireNonNull(super.rt);

    @FeatureState
    public final @NotNull LedState
            led = this.getState(FEATURE_LED);
    /* @formatter:on */

    /**
     * @param adapterSupplier the Pro Controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public NxProController(@NotNull AdapterSupplier<NxProController> adapterSupplier) {
        super("nx_pro", adapterSupplier,
                STICK_LS, STICK_RS, TRIGGER_ZL, TRIGGER_ZR);
    }

}
