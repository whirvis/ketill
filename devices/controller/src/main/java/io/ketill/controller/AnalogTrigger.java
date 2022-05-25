package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An I/O feature representing an analog trigger on a {@link Controller}.
 *
 * @see ControllerButton
 * @see AnalogStick
 */
public final class AnalogTrigger
        extends IoFeature<TriggerStateZ, TriggerState> {

    private static final float TRIGGER_PRESS = 2.0F / 3.0F;

    /**
     * @param force the analog trigger force.
     * @return {@code true} if {@code force} indicates the analog trigger
     * is currently pressed, {@code false} otherwise.
     */
    public static boolean isPressed(float force) {
        return force >= TRIGGER_PRESS;
    }

    private final @Nullable AnalogTriggerCalibration baseCalibration;

    /**
     * @param id              the analog trigger ID.
     * @param baseCalibration the calibration to use when creating a state
     *                        container for this analog trigger. Note that
     *                        the state can use a different calibration
     *                        after creation.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogTrigger(@NotNull String id,
                         @Nullable AnalogTriggerCalibration baseCalibration) {
        super(Controller.class, id);
        this.baseCalibration = baseCalibration;
    }

    /**
     * Constructs a new {@code AnalogTrigger} with no base calibration.
     * Note that a state can use a different calibration after creation.
     *
     * @param id the analog trigger ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogTrigger(@NotNull String id) {
        this(id, null);
    }

    /**
     * @return the calibration to use when creating a state for this
     * analog trigger. Note that the state can use a different calibration
     * after creation.
     * @see TriggerState#useCalibration(AnalogTriggerCalibration)
     */
    public @Nullable AnalogTriggerCalibration getBaseCalibration() {
        return this.baseCalibration;
    }

    @Override
    protected @NotNull TriggerStateZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new TriggerStateZ(this, observer, baseCalibration);
    }

    @Override
    protected @NotNull TriggerState getContainerState(@NotNull TriggerStateZ internalState) {
        return new TriggerState(internalState);
    }

}
