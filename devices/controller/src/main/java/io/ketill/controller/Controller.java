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
import java.util.Objects;

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

    public static final long DISABLE_HOLD = -1L;

    private final @NotNull Map<DeviceButton, DeviceButtonMonitor> deviceButtons;
    private final @NotNull Map<RumbleMotor, Vibration1f> rumbleMotors;

    private long holdTime;
    private long holdPressInterval;

    /* package-private for monitor access */
    @Nullable DeviceButtonCallback<? super Controller> buttonCallback;

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
        this.deviceButtons = new HashMap<>();
        this.rumbleMotors = new HashMap<>();

        this.holdTime = 1000L;
        this.holdPressInterval = 100L;

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
        if (feature instanceof DeviceButton) {
            DeviceButton button = (DeviceButton) registered.feature;
            DeviceButtonMonitor monitor =
                    new DeviceButtonMonitor(this, button);
            synchronized (deviceButtons) {
                deviceButtons.put(button, monitor);
            }
        } else if (feature instanceof RumbleMotor) {
            RumbleMotor motor = (RumbleMotor) registered.feature;
            Vibration1f vibration = (Vibration1f) registered.state;
            synchronized (rumbleMotors) {
                rumbleMotors.put(motor, vibration);
            }
        }
        return registered;
    }
    /* @formatter:on */

    @Override
    public void unregisterFeature(@NotNull IoFeature<?> feature) {
        super.unregisterFeature(feature);
        if (feature instanceof DeviceButton) {
            synchronized (deviceButtons) {
                deviceButtons.remove(feature);
            }
        } else if (feature instanceof RumbleMotor) {
            synchronized (rumbleMotors) {
                rumbleMotors.remove(feature);
            }
        }
    }

    /**
     * Sets the callback for when a {@link DeviceButton} related event
     * occurs (e.g., when a button is pressed or released).
     *
     * @param callback the code to execute when a {@link DeviceButton} related
     *                 event occurs. A value of {@code null} is permitted, and
     *                 will result in nothing being executed.
     * @see #getHoldTime()
     * @see #getHoldPressInterval()
     */
    public void onDeviceButtonEvent(@Nullable DeviceButtonCallback<?
            super Controller> callback) {
        this.buttonCallback = callback;
    }

    /**
     * @return {@code true} if feature holding is enabled for this
     * controller, {@code false} otherwise.
     */
    public boolean isHoldEnabled() {
        return this.holdTime >= 0;
    }

    /**
     * @return how long in milliseconds a feature must be pressed to be
     * considered held down. A negative value indicates that this is
     * disabled for the controller.
     */
    public long getHoldTime() {
        return this.holdTime;
    }

    /**
     * Sets how long in milliseconds a feature must be pressed for it to
     * be considered held down. Once a feature is held down, the feature
     * press callback (if set) will be automatically fired. The virtual
     * press rate is determined by {@link #getHoldPressInterval()}.
     *
     * @param holdTime how long in milliseconds a feature must be
     *                 pressed to be considered held down. A negative
     *                 value indicates that this is disabled for the
     *                 controller.
     * @see #setHoldPressInterval(long)
     */
    public void setHoldTime(long holdTime) {
        this.holdTime = holdTime;
    }

    /**
     * @return how many milliseconds should pass between virtual feature
     * presses when a feature is being held down.
     */
    public long getHoldPressInterval() {
        return this.holdPressInterval;
    }

    /**
     * Sets how many milliseconds should pass between virtual feature
     * presses when a feature is considered to be held down. While not
     * recommended, {@code holdPressInterval} may be as low as one.
     * The time required for a feature to be considered held down is
     * determined by {@link #getHoldTime()}.
     *
     * @param holdPressInterval how many milliseconds should pass
     *                          between virtual feature presses.
     * @throws IllegalArgumentException if {@code holdPressInterval}
     *                                  is less than one.
     * @see #setHoldTime(long)
     */
    public void setHoldPressInterval(long holdPressInterval) {
        if (holdPressInterval < 1) {
            String msg = "holdPressInterval cannot be less than one";
            throw new IllegalArgumentException(msg);
        }
        this.holdPressInterval = holdPressInterval;
    }

    /**
     * Returns if a button is currently being held down.
     * <p>
     * A button is considered held down if it has been pressed for a time
     * longer than or equal to the value returned by {@link #getHoldTime()}.
     *
     * @param button the button to check.
     * @return {@code true} if {@code button} is currently being held down,
     * {@code false} otherwise. If {@link #getHoldTime()} returns a negative
     * value, this method will always return {@code false}.
     * @throws NullPointerException if {@code button} is {@code null}.
     * @see #setHoldTime(long)
     * @see #setHoldPressInterval(long)
     */
    public boolean isHeld(@NotNull DeviceButton button) {
        Objects.requireNonNull(button, "button cannot be null");
        if (this.isHoldEnabled()) {
            return false;
        }
        DeviceButtonMonitor monitor = deviceButtons.get(button);
        return monitor != null && monitor.isButtonHeld();
    }

    /**
     * Returns if a button is currently being held down.
     * <p>
     * A button is considered held down if it has been pressed for a time
     * longer than or equal to the value returned by {@link #getHoldTime()}.
     *
     * @param buttonState the state of the button to check.
     * @return {@code true} if {@code buttonState} is currently being held
     * down, {@code false} otherwise. If feature holding is disabled, this
     * method will always return {@code false}.
     * @throws NullPointerException if {@code buttonState} is {@code null}.
     * @see #isHoldEnabled()
     * @see #setHoldTime(long)
     */
    public boolean isHeld(@NotNull Button1bc buttonState) {
        Objects.requireNonNull(buttonState, "buttonState cannot be null");
        if (this.isHoldEnabled()) {
            return false;
        }

        IoFeature<?> feature = this.getFeature(buttonState);
        if (!(feature instanceof DeviceButton)) {
            return false;
        } else {
            return this.isHeld((DeviceButton) feature);
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

    @Override
    public void poll() {
        long currentTime = System.currentTimeMillis();

        super.poll();

        synchronized (deviceButtons) {
            for (DeviceButtonMonitor monitor : deviceButtons.values()) {
                monitor.fireEvents(currentTime);
            }
        }
    }

}
