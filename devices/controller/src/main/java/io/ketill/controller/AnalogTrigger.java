package io.ketill.controller;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing an analog trigger on a {@link Controller}.
 *
 * @see DeviceButton
 * @see AnalogStick
 */
public class AnalogTrigger extends IoFeature<TriggerStateZ, TriggerState> {

    private static final float TRIGGER_PRESS = 2.0F / 3.0F;

    /**
     * @param force the analog trigger force.
     * @return {@code true} if {@code force} indicates the analog trigger
     * is currently pressed, {@code false} otherwise.
     */
    public static boolean isPressed(float force) {
        return force >= TRIGGER_PRESS;
    }

    /**
     * @param id the analog trigger ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogTrigger(@NotNull String id) {
        super(id);
    }

    @Override
    protected @NotNull TriggerStateZ getInternalState() {
        return new TriggerStateZ();
    }

    @Override
    protected @NotNull TriggerState getContainerState(@NotNull TriggerStateZ internalState) {
        return new TriggerState(internalState);
    }

}
