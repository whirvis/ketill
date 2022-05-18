package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Nintendo Switch right Joy-Con.
 */
public class NxRightJoyCon extends NxJoyCon {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_A = new ControllerButton("a"),
            BUTTON_X = new ControllerButton("x"),
            BUTTON_B = new ControllerButton("b"),
            BUTTON_Y = new ControllerButton("y"),
            BUTTON_PLUS = new ControllerButton("plus"),
            BUTTON_R_THUMB = new ControllerButton("r_thumb"),
            BUTTON_HOME = new ControllerButton("home"),
            BUTTON_R = new ControllerButton("r"),
            BUTTON_ZR = new ControllerButton("zr");

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_RS = new AnalogStick("rs");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_ZR = new AnalogTrigger("zr");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            a = this.getState(BUTTON_A),
            x = this.getState(BUTTON_X),
            b = this.getState(BUTTON_B),
            y = this.getState(BUTTON_Y),
            plus = this.getState(BUTTON_PLUS),
            rThumb = this.getState(BUTTON_R_THUMB),
            home = this.getState(BUTTON_HOME),
            r = this.getState(BUTTON_R),
            zr = this.getState(BUTTON_ZR);

    @FeatureState
    public final @NotNull StickPos
            rs = Objects.requireNonNull(super.rs);

    @FeatureState
    public final @NotNull TriggerState
            rt = Objects.requireNonNull(super.rt);
    /* @formatter:on */

    /**
     * @param adapterSupplier the right Joy-Con adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public NxRightJoyCon(@NotNull AdapterSupplier<NxRightJoyCon> adapterSupplier) {
        super("nx_joycon_right", adapterSupplier,
                null, STICK_RS, null, TRIGGER_ZR);
    }

}
