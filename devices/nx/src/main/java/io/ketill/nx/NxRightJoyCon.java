package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1bc;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.Led1i;
import io.ketill.controller.PlayerLed;
import org.jetbrains.annotations.NotNull;

/**
 * A Nintendo Switch right Joy-Con.
 */
public class NxRightJoyCon extends NxJoyCon {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_A = new DeviceButton("a"),
            BUTTON_X = new DeviceButton("x"),
            BUTTON_B = new DeviceButton("b"),
            BUTTON_Y = new DeviceButton("y"),
            BUTTON_SL = new DeviceButton("sl"),
            BUTTON_SR = new DeviceButton("sr"),
            BUTTON_PLUS = new DeviceButton("plus"),
            BUTTON_R_THUMB = new DeviceButton("r_thumb"),
            BUTTON_HOME = new DeviceButton("home"),
            BUTTON_R = new DeviceButton("r"),
            BUTTON_ZR = new DeviceButton("zr");

    @FeaturePresent
    public static final @NotNull AnalogStick
            STICK_RS = new AnalogStick("rs");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_ZR = new AnalogTrigger("zr");

    @FeaturePresent
    public static final @NotNull PlayerLed
            FEATURE_LED = new PlayerLed("led");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            a = this.getState(BUTTON_A),
            x = this.getState(BUTTON_X),
            b = this.getState(BUTTON_B),
            y = this.getState(BUTTON_Y),
            sl = this.getState(BUTTON_SL),
            sr = this.getState(BUTTON_SR),
            plus = this.getState(BUTTON_PLUS),
            rThumb = this.getState(BUTTON_R_THUMB),
            home = this.getState(BUTTON_HOME),
            r = this.getState(BUTTON_R),
            zr = this.getState(BUTTON_ZR);

    @FeatureState
    public final @NotNull Led1i
            led = this.getState(FEATURE_LED);
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
