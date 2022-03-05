package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1bc;
import io.ketill.controller.DeviceButton;
import org.jetbrains.annotations.NotNull;

/**
 * A Nintendo Switch left Joy-Con.
 */
public class NxLeftJoyCon extends NxJoyCon {

    /* @formatter:off */
    @FeaturePresent
    public static final DeviceButton
            BUTTON_LEFT = new DeviceButton("left"),
            BUTTON_DOWN = new DeviceButton("down"),
            BUTTON_UP = new DeviceButton("up"),
            BUTTON_RIGHT = new DeviceButton("right"),
            BUTTON_MINUS = new DeviceButton("minus"),
            BUTTON_L_THUMB = new DeviceButton("l_thumb"),
            BUTTON_SNAPSHOT = new DeviceButton("snapshot"),
            BUTTON_L = new DeviceButton("l"),
            BUTTON_ZL = new DeviceButton("zl");

    @FeaturePresent
    public static final AnalogStick
            STICK_LS = new AnalogStick("ls");

    @FeaturePresent
    public static final AnalogTrigger
            TRIGGER_ZL = new AnalogTrigger("zl");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            left = this.getState(BUTTON_LEFT),
            down = this.getState(BUTTON_DOWN),
            up = this.getState(BUTTON_UP),
            right = this.getState(BUTTON_RIGHT),
            minus = this.getState(BUTTON_MINUS),
            lThumb = this.getState(BUTTON_L_THUMB),
            snapshot = this.getState(BUTTON_SNAPSHOT),
            l = this.getState(BUTTON_L),
            zl = this.getState(BUTTON_ZL);
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
