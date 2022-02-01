package com.whirvis.ketill.psx;

import com.whirvis.ketill.AdapterSupplier;
import com.whirvis.ketill.AnalogTrigger;
import com.whirvis.ketill.Button1bc;
import com.whirvis.ketill.DeviceButton;
import com.whirvis.ketill.FeaturePresent;
import com.whirvis.ketill.FeatureState;
import com.whirvis.ketill.RumbleMotor;
import com.whirvis.ketill.Vibration1f;
import org.jetbrains.annotations.NotNull;

/**
 * A Sony PlayStation 3 controller.
 */
public class Ps3Controller extends PsxController {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_SELECT = new DeviceButton("select"),
            BUTTON_START = new DeviceButton("start");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_STRONG = new RumbleMotor("rumble_strong"),
            MOTOR_WEAK = new RumbleMotor("rumble_weak");
    /* @formatter:on */

    /* TODO: player LED feature (present in wii-hid branch) */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            select = this.getState(BUTTON_SELECT),
            start = this.getState(BUTTON_START);

    @FeatureState
    public final @NotNull Vibration1f
            rumbleStrong = this.getState(MOTOR_STRONG),
            rumbleWeak = this.getState(MOTOR_WEAK);
    /* @formatter:on */

    public Ps3Controller(@NotNull AdapterSupplier<Ps3Controller> adapterSupplier) {
        super("ps3", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

}
