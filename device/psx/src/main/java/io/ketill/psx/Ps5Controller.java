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
import io.ketill.controller.SensorValue;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Sony PlayStation 5 controller.
 */
public class Ps5Controller extends PsxController {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_SHARE = new ControllerButton("share"),
            BUTTON_OPTIONS = new ControllerButton("options"),
            BUTTON_PS = new ControllerButton("playstation"),
            BUTTON_TPAD = new ControllerButton("trackpad"),
            BUTTON_MUTE = new ControllerButton("mute");

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
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            share = this.getState(BUTTON_SHARE),
            options = this.getState(BUTTON_OPTIONS),
            ps = this.getState(BUTTON_PS),
            tpad = this.getState(BUTTON_TPAD),
            mute = this.getState(BUTTON_MUTE);

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
    /* @formatter:on */

    /**
     * Constructs a new {@code Ps5Controller}.
     *
     * @param adapterSupplier the PlayStation 5 controller adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Ps5Controller(@NotNull AdapterSupplier<Ps5Controller> adapterSupplier) {
        super("ps5", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

}
