package io.ketill.nx;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.BatteryLevel;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.GenericSensor;
import io.ketill.controller.InternalBattery;
import io.ketill.controller.LedState;
import io.ketill.controller.PlayerLed;
import io.ketill.controller.SensorValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Nintendo Switch Joy-Con.
 *
 * @see #asLeftJoyCon()
 * @see #asRightJoyCon()
 */
public abstract class NxJoyCon extends Controller {

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull ControllerButton
            BUTTON_SL = new ControllerButton("sl"),
            BUTTON_SR = new ControllerButton("sr");

    @FeaturePresent
    public static final @NotNull GenericSensor
            SENSOR_ACCELEROMETER = new GenericSensor("accelerometer"),
            SENSOR_GYROSCOPE = new GenericSensor("gyroscope");

    @FeaturePresent
    public static final @NotNull InternalBattery
            INTERNAL_BATTERY = new InternalBattery("battery");

    @FeaturePresent
    public static final @NotNull PlayerLed
            FEATURE_LED = new PlayerLed("led");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull ButtonState
            sl = this.getState(BUTTON_SL),
            sr = this.getState(BUTTON_SR);

    @FeatureState
    public final @NotNull SensorValue
            accelerometer = this.getState(SENSOR_ACCELEROMETER),
            gyroscope = this.getState(SENSOR_GYROSCOPE);

    @FeatureState
    public final @NotNull BatteryLevel
            battery = this.getState(INTERNAL_BATTERY);

    @FeatureState
    public final @NotNull LedState
            led = this.getState(FEATURE_LED);
    /* @formatter:on */

    /**
     * Constructs a new {@code NxJoyCon}.
     * <p>
     * <b>Note:</b> This constructor is package-private so only this module
     * can extend from this class. However, the class is kept public so users
     * can generalize left and right Joy-Cons.
     *
     * @param id              the Joy-Con ID.
     * @param adapterSupplier the Joy-Con adapter supplier.
     * @param ls              the left analog stick, may be {@code null}.
     * @param rs              the right analog stick, may be {@code null}.
     * @param lt              the left analog trigger, may be {@code null}.
     * @param rt              the right analog trigger, may be {@code null}.
     * @throws NullPointerException     if {@code id} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}; if the adapter
     *                                  given by {@code adapterSupplier}
     *                                  is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    NxJoyCon(@NotNull String id, @NotNull AdapterSupplier<?> adapterSupplier,
             @Nullable AnalogStick ls, @Nullable AnalogStick rs,
             @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt) {
        super(id, adapterSupplier, ls, rs, lt, rt);
    }

    /**
     * Returns if this is a left Joy-Con.
     *
     * @return {@code true} if this a left Joy-Con, {@code false} otherwise.
     * @see #asLeftJoyCon()
     */
    public final boolean isLeftJoyCon() {
        return this instanceof NxLeftJoyCon;
    }

    /**
     * Returns this instance as an {@link NxLeftJoyCon}.
     *
     * @return this instance as an {@link NxLeftJoyCon}.
     * @throws UnsupportedOperationException if this is not a left Joy-Con.
     * @see #isLeftJoyCon()
     */
    public final @NotNull NxLeftJoyCon asLeftJoyCon() {
        if (!this.isLeftJoyCon()) {
            throw new UnsupportedOperationException("not a left JoyCon");
        }
        return (NxLeftJoyCon) this;
    }

    /**
     * Returns if this is a right Joy-Con.
     *
     * @return {@code true} if this a right Joy-Con, {@code false} otherwise.
     * @see #asRightJoyCon()
     */
    public final boolean isRightJoyCon() {
        return this instanceof NxRightJoyCon;
    }

    /**
     * Returns this instance as an {@link NxRightJoyCon}.
     *
     * @return this instance as an {@link NxRightJoyCon}.
     * @throws UnsupportedOperationException if this is not a right Joy-Con.
     * @see #isRightJoyCon()
     */
    public final @NotNull NxRightJoyCon asRightJoyCon() {
        if (!this.isRightJoyCon()) {
            throw new UnsupportedOperationException("not a right JoyCon");
        }
        return (NxRightJoyCon) this;
    }

}
