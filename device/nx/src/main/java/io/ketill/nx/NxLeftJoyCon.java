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
 * A Nintendo Switch left Joy-Con.
 */
public class NxLeftJoyCon extends NxJoyCon {

    /* @formatter:off */
    @FeaturePresent
    public static final ControllerButton
            BUTTON_LEFT = new ControllerButton("left"),
            BUTTON_DOWN = new ControllerButton("down"),
            BUTTON_UP = new ControllerButton("up"),
            BUTTON_RIGHT = new ControllerButton("right"),
            BUTTON_MINUS = new ControllerButton("minus"),
            BUTTON_L_THUMB = new ControllerButton("l_thumb"),
            BUTTON_SNAPSHOT = new ControllerButton("snapshot"),
            BUTTON_L = new ControllerButton("l"),
            BUTTON_ZL = new ControllerButton("zl");

    @FeaturePresent
    public static final AnalogStick
            STICK_LS = new AnalogStick("ls");

    /**
     * This trigger acts as a wrapper for {@link #BUTTON_ZL}.
     */
    @FeaturePresent
    public static final AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            left = this.getState(BUTTON_LEFT),
            down = this.getState(BUTTON_DOWN),
            up = this.getState(BUTTON_UP),
            right = this.getState(BUTTON_RIGHT),
            minus = this.getState(BUTTON_MINUS),
            lThumb = this.getState(BUTTON_L_THUMB),
            snapshot = this.getState(BUTTON_SNAPSHOT),
            l = this.getState(BUTTON_L),
            zl = this.getState(BUTTON_ZL);

    @FeatureState
    public final @NotNull StickPos
            ls = Objects.requireNonNull(super.ls);

    /**
     * The force of this trigger is dependent on the state of {@link #zl}.
     * If the button is pressed, the force of this trigger will be
     * {@code 1.0F}. Otherwise, it will be {@code 0.0F}.
     *
     * @see #TRIGGER_LT
     */
    @FeatureState
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt);
    /* @formatter:on */

    /**
     * Constructs a new {@code NxLeftJoyCon}.
     *
     * @param adapterSupplier the left Joy-Con adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public NxLeftJoyCon(@NotNull AdapterSupplier<NxLeftJoyCon> adapterSupplier) {
        super("nx_joycon_left", adapterSupplier,
                STICK_LS, null, TRIGGER_LT, null);
    }

}
