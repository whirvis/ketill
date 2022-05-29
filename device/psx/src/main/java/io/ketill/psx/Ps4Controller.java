package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.GenericSensor;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.SensorValue;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Sony PlayStation 4 controller.
 */
public class Ps4Controller extends PsxController {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_SHARE = new ControllerButton("share"),
            BUTTON_OPTIONS = new ControllerButton("options"),
            BUTTON_PS = new ControllerButton("playstation"),
            BUTTON_TPAD = new ControllerButton("trackpad");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull GenericSensor
            SENSOR_ACCELEROMETER = new GenericSensor("accelerometer"),
            SENSOR_GYROSCOPE = new GenericSensor("gyroscope");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_STRONG = new RumbleMotor("rumble_strong"),
            MOTOR_WEAK = new RumbleMotor("rumble_weak");

    @FeaturePresent
    public static final @NotNull Lightbar
            FEATURE_LIGHTBAR = new Lightbar("lightbar");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            share = this.getState(BUTTON_SHARE),
            options = this.getState(BUTTON_OPTIONS),
            ps = this.getState(BUTTON_PS),
            tpad = this.getState(BUTTON_TPAD);

    @FeatureState
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt),
            rt = Objects.requireNonNull(super.rt);

    @FeatureState
    public final @NotNull SensorValue
            accelerometer = this.getState(SENSOR_ACCELEROMETER),
            gyroscope = this.getState(SENSOR_GYROSCOPE);

    @FeatureState
    public final @NotNull MotorVibration
            rumbleStrong = this.getState(MOTOR_STRONG),
            rumbleWeak = this.getState(MOTOR_WEAK);

    @FeatureState
    public final @NotNull LightbarColor
            lightbar = this.getState(FEATURE_LIGHTBAR);
    /* @formatter:on */

    /**
     * Constructs a new {@code Ps4Controller}.
     *
     * @param adapterSupplier the PlayStation 4 controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Ps4Controller(@NotNull AdapterSupplier<Ps4Controller> adapterSupplier) {
        super("ps4", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

}
