package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Device features provide a definition of a capability present on an
 * {@link InputDevice}. Some examples would be a button, an analog stick, a
 * rumble motor, or an LED indicator. Depending on the feature, their state
 * can be either read-only (e.g., button state) or read-write (e.g., rumble
 * motor vibration).
 *
 * @param <S> the state container type.
 * @see InputDevice#registerFeature(DeviceFeature)
 */
public abstract class DeviceFeature<S> {

    public final @NotNull String id;
    public final @NotNull Supplier<S> initialState;

    /**
     * @param id           the feature ID.
     * @param initialState a supplier for the feature's initial state.
     */
    public DeviceFeature(@NotNull String id,
                         @NotNull Supplier<S> initialState) {
        this.id = id;
        this.initialState = initialState;
    }

}
