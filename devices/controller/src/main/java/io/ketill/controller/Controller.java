package io.ketill.controller;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.IoDevice;
import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

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
public abstract class Controller extends IoDevice {

    private final @NotNull Map<RumbleMotor, Vibration1f> rumbleMotors;

    /**
     * The left and right analog sticks of the controller.<br>
     * These may not be present, and as such may be {@code null}.
     */
    public final @Nullable Vector3fc ls, rs;

    /**
     * The left and right analog triggers of the controller.<br>
     * These may not be present, and as such may be {@code null}.
     */
    public final @Nullable Trigger1fc lt, rt;

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
     */
    public Controller(@NotNull String id,
                      @NotNull AdapterSupplier<?> adapterSupplier,
                      @Nullable AnalogStick ls, @Nullable AnalogStick rs,
                      @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt,
                      boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, false, false);
        this.rumbleMotors = new HashMap<>();

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
            registerAndGetState(@Nullable IoFeature<S> feature) {
        if (feature == null) {
            return null;
        }
        if (!this.isRegistered(feature)) {
            this.registerFeature(feature);
        }
        return this.getState(feature);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override
    public <F extends IoFeature<S>, S> @NotNull RegisteredFeature<F, S>
            registerFeature(@NotNull F feature) {
        RegisteredFeature<F, S> registered = super.registerFeature(feature);
        if (feature instanceof RumbleMotor) {
            synchronized (rumbleMotors) {
                rumbleMotors.put((RumbleMotor) feature,
                        (Vibration1f) this.getState(feature));
            }
        }
        return registered;
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull IoFeature<?> feature) {
        super.unregisterFeature(feature);
        if (feature instanceof RumbleMotor) {
            synchronized (rumbleMotors) {
                rumbleMotors.remove(feature);
            }
        }
    }

    /**
     * Sets the vibration force of each rumble motor. To prevent unexpected
     * behavior, the force is capped between a value of {@code 0.0F} and
     * {@code 1.0F}.
     *
     * @param force the vibration force to set each motor to.
     */
    public void rumble(float force) {
        float capped = Math.min(Math.max(force, 0.0F), 1.0F);
        synchronized (rumbleMotors) {
            for (Vibration1f vibration : rumbleMotors.values()) {
                vibration.force = capped;
            }
        }
    }

}
