package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1bc;
import io.ketill.controller.DeviceButton;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.Vibration1f;
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