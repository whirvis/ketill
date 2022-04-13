package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An {@link IoFeature} which has an internal state identical to the
 * container state. This is useful for features which store no data
 * that needs to be hidden from the user.
 *
 * @param <S> the state container type.
 * @see IoDevice#registerFeature(IoFeature)
 * @see FeaturePresent
 * @see FeatureState
 */
public class SimpleIoFeature<S> extends IoFeature<S, S> {

    private final @NotNull Supplier<@NotNull S> initialState;

    /**
     * @param id           the feature ID.
     * @param initialState a supplier for the feature's initial state.
     * @throws NullPointerException     if {@code id}, {@code initialState} or
     *                                  the value that {@code initialState}
     *                                  supplies is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public SimpleIoFeature(@NotNull String id,
                           @NotNull Supplier<@NotNull S> initialState) {
        super(id);
        this.initialState = Objects.requireNonNull(initialState,
                "initialState cannot be null");
        Objects.requireNonNull(initialState.get(),
                "value supplied by initialState cannot be null");
    }

    @Override
    protected final @NotNull S getInternalState() {
        return initialState.get();
    }

    @Override
    protected final @NotNull S getContainerState(@NotNull S internalState) {
        return internalState;
    }

}
