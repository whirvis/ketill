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
            BUTTON_L = new ControllerButton("l");

    @FeaturePresent
    public static final AnalogStick
            STICK_LS = new AnalogStick("ls");

    /**
     * This feature is presented as an {@link AnalogTrigger} (rather than a
     * {@link ControllerButton}) even though it can only be fully pressed or
     * fully released. This is because most other controllers have a proper
     * analog trigger, so this continues the pattern.
     * <p>
     * Feature adapters should set the force to {@code 1.0F} when the ZL
     * button is pressed, and {@code 0.0F} when it is released.
     */
    @FeaturePresent
    public static final AnalogTrigger
            TRIGGER_ZL = new AnalogTrigger("zl");
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
            l = this.getState(BUTTON_L);

    @FeatureState
    public final @NotNull StickPos
            ls = Objects.requireNonNull(super.ls);

    @FeatureState
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt);
    /* @formatter:on */

    /**
     * @param adapterSupplier the left Joy-Con adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public NxLeftJoyCon(@NotNull AdapterSupplier<NxLeftJoyCon> adapterSupplier) {
        super("nx_joycon_left", adapterSupplier,
                STICK_LS, null, TRIGGER_ZL, null);
    }

}
