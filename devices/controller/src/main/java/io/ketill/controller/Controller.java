package io.ketill.controller;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.IoDevice;
import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import io.ketill.pressable.PressableIoFeatureConfig;
import io.ketill.pressable.PressableIoFeatureConfigView;
import io.ketill.pressable.PressableIoFeatureSupport;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A controller which and can send receive I/O data. Examples of controllers
 * include, but are not limited to: XBOX controllers, PlayStation controllers,
 * Nintendo Switch controllers, etc.
 * <p>
 * <b>Note:</b> For data to stay up-to-date, the controller must be polled
 * periodically via the {@link #poll()} method. It is recommended to poll
 * the controller once every application update.
 */
public abstract class Controller extends IoDevice
        implements PressableIoFeatureSupport {

    private final @NotNull Map<RumbleMotor, MotorVibration> rumbleMotors;
    private @NotNull PressableIoFeatureConfigView pressableConfig;

    /**
     * The left and right analog sticks of the controller.<br>
     * These may not be present, and as such may be {@code null}.
     */
    public final @Nullable StickPos ls, rs;

    /**
     * The left and right analog triggers of the controller.<br>
     * These may not be present, and as such may be {@code null}.
     */
    public final @Nullable TriggerState lt, rt;

    /**
     * Constructs a new {@code Controller}. If {@code ls}, {@code rs},
     * {@code lt}, or {@code rt} are not {@code null}, they will be
     * registered automatically during construction (assuming they are not
     * already registered via the {@link FeaturePresent} annotation.)
     *
     * @param id              the controller ID.
     * @param adapterSupplier the controller adapter supplier.
     * @param ls              the left analog stick, may be {@code null}.
     * @param rs              the right analog stick, may be {@code null}.
     * @param lt              the left analog trigger, may be {@code null}.
     * @param rt              the right analog trigger, may be {@code null}.
     * @param registerFields  {@code true} if the constructor should call
     *                        {@link #registerFields()}. If {@code false},
     *                        the extending class must call it if it desires
     *                        the functionality of {@link FeaturePresent}.
     * @param initAdapter     {@code true} if the constructor should call
     *                        {@link #initAdapter()}. If {@code false}, the
     *                        extending class <b>must</b> call it.
     * @throws NullPointerException     if {@code id} or
     *                                  {@code adapterSupplier}
     *                                  are {@code null}; if the adapter
     *                                  given by {@code adapterSupplier}
     *                                  is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public Controller(@NotNull String id,
                      @NotNull AdapterSupplier<?> adapterSupplier,
                      @Nullable AnalogStick ls, @Nullable AnalogStick rs,
                      @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt,
                      boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, false, false);

        this.rumbleMotors = new HashMap<>();
        this.pressableConfig = PressableIoFeatureConfig.DEFAULT;

        if (registerFields) {
            this.registerFields();
        }

        this.ls = this.registerAndGetState(ls);
        this.rs = this.registerAndGetState(rs);
        this.lt = this.registerAndGetState(lt);
        this.rt = this.registerAndGetState(rt);

        if (initAdapter) {
            this.initAdapter();
        }
    }

    /**
     * Constructs a new {@code Controller}. If {@code ls}, {@code rs},
     * {@code lt}, or {@code rt} are not {@code null}, they will be
     * registered automatically during construction (assuming they are not
     * already registered via the {@link FeaturePresent} annotation.)
     * <p>
     * This is a shorthand for the base constructor with the argument for
     * {@code registerFields} and {@code initAdapter} being {@code true}.
     *
     * @param id              the controller ID.
     * @param adapterSupplier the controller adapter supplier.
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
    public Controller(@NotNull String id,
                      @NotNull AdapterSupplier<?> adapterSupplier,
                      @Nullable AnalogStick ls, @Nullable AnalogStick rs,
                      @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt) {
        this(id, adapterSupplier, ls, rs, lt, rt, true, true);
    }

    /**
     * A method for a special edge case; that being if a feature is not
     * already registered to the controller when specified at construction.
     * This method exists solely to prevent users from needing to use the
     * {@link FeaturePresent} annotation when extending {@code Controller}.
     *
     * @param feature the feature whose state to fetch, and to register if
     *                not already registered to this controller.
     * @param <S>     the state container type.
     * @return the state as returned by {@link #getState(IoFeature)},
     * {@code null} if {@code feature} is {@code null}.
     */
    /* @formatter:off */
    private <S> @Nullable S
            registerAndGetState(@Nullable IoFeature<?, S> feature) {
        if (feature == null) {
            return null;
        } else if (!this.isFeatureRegistered(feature)) {
            this.registerFeature(feature);
        }
        return this.getState(feature);
    }
    /* @formatter:on */

    @Override /* overridden for visibility in ControllerTest */
    protected <Z> Z getInternalState(@NotNull IoFeature<Z, ?> feature) {
        return super.getInternalState(feature);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void featureRegistered(@NotNull RegisteredFeature<?, ?, ?> registered,
                                     @NotNull Object internalState) {
        if (registered.feature instanceof RumbleMotor) {
            RumbleMotor motor = (RumbleMotor) registered.feature;
            MotorVibration vibration = (MotorVibration) internalState;
            synchronized (rumbleMotors) {
                rumbleMotors.put(motor, vibration);
            }
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void featureUnregistered(@NotNull IoFeature<?, ?> feature) {
        if (feature instanceof RumbleMotor) {
            synchronized (rumbleMotors) {
                MotorVibration vibration = rumbleMotors.remove(feature);
                vibration.setStrength(0.0F);
            }
        }
    }

    @Override
    public final void usePressableConfig(@Nullable PressableIoFeatureConfigView view) {
        this.pressableConfig = PressableIoFeatureConfig.valueOf(view);
    }

    @Override
    public final @NotNull PressableIoFeatureConfigView getPressableConfig() {
        return this.pressableConfig;
    }

    /**
     * Sets the vibration force of each rumble motor.
     *
     * @param strength the vibration strength to set each motor to. This
     *                 value will be capped to a range of {@code 0.0F}
     *                 to {@code 1.0F} to prevent unexpected behaviors.
     */
    public void rumble(float strength) {
        synchronized (rumbleMotors) {
            for (MotorVibration vibration : rumbleMotors.values()) {
                vibration.setStrength(strength);
            }
        }
    }

}
