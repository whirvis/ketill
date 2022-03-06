package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * I/O features provide a definition of a capability present on an
 * {@link IoDevice}. Some examples would be a button, an analog stick, a
 * rumble motor, or an LED indicator. Depending on the feature, their state
 * can be either read-only (e.g., button state) or read-write (e.g., rumble
 * motor vibration).
 *
 * @param <S> the state container type.
 * @see IoDevice#registerFeature(IoFeature)
 * @see FeaturePresent
 * @see FeatureState
 */
public class IoFeature<S> {

    public final @NotNull String id;
    public final @NotNull Supplier<@NotNull S> initialState;

    /**
     * @param id           the feature ID.
     * @param initialState a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code id}, {@code initialState} or
     *                                  the value that {@code initialState}
     *                                  supplies is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public IoFeature(@NotNull String id,
                     @NotNull Supplier<@NotNull S> initialState) {
        this.id = Objects.requireNonNull(id, "id");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id cannot contain whitespace");
        }

        /* @formatter:off */
        this.initialState = Objects.requireNonNull(initialState,
                "initialState");
        Objects.requireNonNull(initialState.get(),
                "supplied initial state is null");
        /* @formatter:on */
    }

}
