package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.BatteryLevel;
import io.ketill.controller.ButtonState;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.GenericSensor;
import io.ketill.controller.InternalBattery;
import io.ketill.controller.LedState;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.PlayerLed;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.SensorValue;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Sony PlayStation 3 controller.
 */
public class Ps3Controller extends PsxController {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_SELECT = new ControllerButton("select"),
            BUTTON_START = new ControllerButton("start");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull GenericSensor
            SENSOR_ACCELEROMETER = new GenericSensor("accelerometer"),
            SENSOR_GYROSCOPE = new GenericSensor("gyroscope");

    @FeaturePresent
    public static final @NotNull InternalBattery
            INTERNAL_BATTERY = new InternalBattery("battery");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_STRONG = new RumbleMotor("rumble_strong"),
            MOTOR_WEAK = new RumbleMotor("rumble_weak");

    @FeaturePresent
    public static final @NotNull PlayerLed
            FEATURE_LED = new PlayerLed("led");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            select = this.getState(BUTTON_SELECT),
            start = this.getState(BUTTON_START);

    @FeatureState
    public final @NotNull TriggerState
            lt = Objects.requireNonNull(super.lt),
            rt = Objects.requireNonNull(super.rt);

    @FeatureState
    public final @NotNull SensorValue
            accelerometer = this.getState(SENSOR_ACCELEROMETER),
            gyroscope = this.getState(SENSOR_GYROSCOPE);

    @FeatureState
    public final @NotNull BatteryLevel
            battery = this.getState(INTERNAL_BATTERY);

    @FeatureState
    public final @NotNull MotorVibration
            rumbleStrong = this.getState(MOTOR_STRONG),
            rumbleWeak = this.getState(MOTOR_WEAK);

    @FeatureState
    public final @NotNull LedState
            led = this.getState(FEATURE_LED);
    /* @formatter:on */

    /**
     * Constructs a new {@code Ps3Controller}.
     *
     * @param adapterSupplier the PlayStation 3 controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Ps3Controller(@NotNull AdapterSupplier<Ps3Controller> adapterSupplier) {
        super("ps3", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

}
