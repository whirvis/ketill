package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.Direction;
import io.ketill.controller.StickPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The base for a Sony PlayStation controller.
 * <p>
 * <b>Note:</b> This class does <i>not</i> represent a PlayStation 1
 * controller. If PlayStation 1 controllers are added in the future,
 * their class will be named {@code Ps1Controller}.
 *
 * @see Ps3Controller
 * @see Ps4Controller
 * @see Ps5Controller
 */
public abstract class PsxController extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_SQUARE = new ControllerButton("square"),
            BUTTON_CROSS = new ControllerButton("cross"),
            BUTTON_CIRCLE = new ControllerButton("circle"),
            BUTTON_TRIANGLE = new ControllerButton("triangle"),
            BUTTON_L1 = new ControllerButton("l1"),
            BUTTON_R1 = new ControllerButton("r1"),
            BUTTON_L2 = new ControllerButton("l2"),
            BUTTON_R2 = new ControllerButton("r2"),
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
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            square = this.getState(BUTTON_SQUARE),
            cross = this.getState(BUTTON_CROSS),
            circle = this.getState(BUTTON_CIRCLE),
            triangle = this.getState(BUTTON_TRIANGLE),
            l1 = this.getState(BUTTON_L1),
            r1 = this.getState(BUTTON_R1),
            l2 = this.getState(BUTTON_L2),
            r2 = this.getState(BUTTON_R2),
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
    /* @formatter:on */

    /**
     * Constructs a new {@code PsxController}.
     *
     * @param typeId          the PlayStation controller type ID.
     * @param lt              the left analog trigger, may be {@code null}.
     * @param rt              the right analog trigger, may be {@code null}.
     * @param adapterSupplier the PlayStation controller adapter supplier.
     * @throws NullPointerException if {@code typeId} or
     *                              {@code adapterSupplier} are {@code null};
     *                              if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public PsxController(@NotNull String typeId,
                         @NotNull AdapterSupplier<?> adapterSupplier,
                         @Nullable AnalogTrigger lt,
                         @Nullable AnalogTrigger rt) {
        super(typeId, adapterSupplier, STICK_LS, STICK_RS, lt, rt);
    }

}
