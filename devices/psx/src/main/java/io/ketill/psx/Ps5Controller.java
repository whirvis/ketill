package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.AnalogTrigger;
import io.ketill.Button1bc;
import io.ketill.DeviceButton;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import org.jetbrains.annotations.NotNull;

/**
 * A Sony PlayStation 5 controller.
 */
public class Ps5Controller extends PsxController {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_SHARE = new DeviceButton("share"),
            BUTTON_OPTIONS = new DeviceButton("options"),
            BUTTON_PS = new DeviceButton("playstation"),
            BUTTON_TPAD = new DeviceButton("trackpad"),
            BUTTON_MUTE = new DeviceButton("mute");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            share = this.getState(BUTTON_SHARE),
            options = this.getState(BUTTON_OPTIONS),
            ps = this.getState(BUTTON_PS),
            tpad = this.getState(BUTTON_TPAD),
            mute = this.getState(BUTTON_MUTE);
    /* @formatter:on */

    public Ps5Controller(@NotNull AdapterSupplier<Ps5Controller> adapterSupplier) {
        super("ps5", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

}
